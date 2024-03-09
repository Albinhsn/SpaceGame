package se.liu.albhe576.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL40.*;

public class Font {
    public final Texture fontTexture;
    public int dynamicVertexArrayId;
    public int dynamicVertexBufferId;
    public static final int TEXT_MAX_LENGTH = 32;
    private final Logger logger = Logger.getLogger("Font");
    static class FontType{
        float left;
        float right;
        int size;
        public FontType(float left, float right, int size){
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }
    private final FontType[] types = new FontType[256];
    private void createTextBuffers(){

        // Create indices for max length quads
       int []indices = new int[Font.TEXT_MAX_LENGTH * 6];
       for(int i = 0, idx = 0; idx < Font.TEXT_MAX_LENGTH * 6; idx += 6, i+=4){
           indices[idx + 0] = i + 0;
           indices[idx + 1] = i + 1;
           indices[idx + 2] = i + 2;
           indices[idx + 3] = i + 0;
           indices[idx + 4] = i + 3;
           indices[idx + 5] = i + 1;
       }

       int vertexCount = Font.TEXT_MAX_LENGTH * 4;
       float[]vertices = new float[vertexCount];
       Arrays.fill(vertices, 0);

       this.dynamicVertexArrayId = glGenVertexArrays();
       glBindVertexArray(this.dynamicVertexArrayId);

       this.dynamicVertexBufferId = glGenBuffers();
       glBindBuffer(GL_ARRAY_BUFFER, this.dynamicVertexBufferId);
       glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

       glEnableVertexAttribArray(0);
       glEnableVertexAttribArray(1);

       glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 5, 0);
       glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 5, 3 * 4);

       int indexBufferId = glGenBuffers();
       glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
       glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

       glBindVertexArray(0);
    }
    public void updateText(String text, float x, float y, float spaceSize, float fontSize, boolean centered){
        int vertexCount = Font.TEXT_MAX_LENGTH * 4 * 5;

        glBindVertexArray(this.dynamicVertexArrayId);
        float [] vertices = this.buildUpdatedTextVertexArray(vertexCount, text, x, y, spaceSize, fontSize, centered);

        glBindBuffer(GL_ARRAY_BUFFER, this.dynamicVertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glBindVertexArray(0);
    }

    private float[] buildUpdatedTextVertexArray(int vertexCount, String text, float x, float y,float spaceSize, float fontSize, boolean centered){
        float[] vertices = new float[vertexCount];
        Arrays.fill(vertices, 0);

        float drawX = x * 0.01f;
        float drawY = (y + fontSize) * 0.01f;
        float height = fontSize * 0.04f;

        float sizeModifier = 0.5f;

        int numLetters = text.length();
        if(numLetters > Font.TEXT_MAX_LENGTH){
            this.logger.severe(String.format("Trying to write text with %d characters, %d is max", numLetters, Font.TEXT_MAX_LENGTH));
            numLetters = Font.TEXT_MAX_LENGTH;
        }

        if(centered){
            float totalSize = 0;
            for(char c : text.toCharArray()){
                float addedSize = this.types[(byte)c].size * 0.01f * sizeModifier;
                totalSize += addedSize != 0 ? addedSize : spaceSize * 0.01f;
            }
            drawX -= totalSize / 2.0f;
        }

        for(int letterIdx = 0, vertexIdx = 0; letterIdx < numLetters; letterIdx++){
            int letter = text.charAt(letterIdx);
            if(letter == 32){
                drawX += spaceSize * 0.01f;
            }else{
                FontType fontType = this.types[letter];
                float size = fontType.size * 0.01f * sizeModifier;

                vertices[vertexIdx + 0] = drawX;
                vertices[vertexIdx + 1] = drawY;
                vertices[vertexIdx + 2] = 0.0f;
                vertices[vertexIdx + 3] = fontType.left;
                vertices[vertexIdx + 4] = 0.0f;
                vertexIdx += 5;

                vertices[vertexIdx + 0] = drawX + size;
                vertices[vertexIdx + 1] = drawY - height;
                vertices[vertexIdx + 2] = 0.0f;
                vertices[vertexIdx + 3] = fontType.right;
                vertices[vertexIdx + 4] = 1.0f;
                vertexIdx += 5;

                vertices[vertexIdx + 0] = drawX;
                vertices[vertexIdx + 1] = drawY - height;
                vertices[vertexIdx + 2] = 0.0f;
                vertices[vertexIdx + 3] = fontType.left;
                vertices[vertexIdx + 4] = 1.0f;
                vertexIdx += 5;

                vertices[vertexIdx + 0] = drawX + size;
                vertices[vertexIdx + 1] = drawY;
                vertices[vertexIdx + 2] = 0.0f;
                vertices[vertexIdx + 3] = fontType.right;
                vertices[vertexIdx + 4] = 0.0f;
                vertexIdx += 5;

                drawX += size + 0.002f;
            }
        }
        return vertices;
    }
    private int skipWhiteSpace(String line,int index){
        while(line.charAt(index) == ' '){
            index++;
        }
        return index;
    }
    private int skipNonWhiteSpace( String line, int index){
        while(line.charAt(index) != ' '){
            index++;
        }
        return index;
    }
    private void parseFontType(String fontTypeFileLocation){
        List<String> lines;
        try{
            lines = Files.readAllLines(Path.of(fontTypeFileLocation));
        }catch(IOException e){
            e.printStackTrace();
            logger.severe(String.format("Failed to parse font type from '%s'", fontTypeFileLocation));
            System.exit(1);
            return;
        }
        for(String line : lines){
            int idx = this.skipNonWhiteSpace(line, 0);
            int index = Integer.parseInt(line.substring(0, idx));

            // Skip whitespace, character and another whitespace
            idx+= 3;

            int startIdx =idx;
            idx = this.skipNonWhiteSpace(line, idx);
            float left = (float)Double.parseDouble(line.substring(startIdx, idx));

            idx = this.skipWhiteSpace(line, idx);

            startIdx = idx;
            idx = skipNonWhiteSpace(line, idx);
            float right = (float) Double.parseDouble(line.substring(startIdx, idx));

            idx = this.skipWhiteSpace(line, idx);
            int size = Integer.parseInt(line.substring(idx));
            this.types[index] = new FontType(left, right, size);
        }
    }

    public Font(ResourceManager resourceManager, String fontFileLocation){
        this.fontTexture = resourceManager.createTexture(fontFileLocation);
    }

    public static Font parseFont(ResourceManager resourceManager, String fontFileLocation, String fontTypeFileLocation){
        Font font = new Font(resourceManager, fontFileLocation);
        font.parseFontType(fontTypeFileLocation);
        font.createTextBuffers();

        return font;
    }

}
