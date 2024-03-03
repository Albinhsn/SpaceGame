package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;


public class Renderer
{
    private final long window;
    private final int screenWidth;
    private final int screenHeight;
    private final ResourceManager resourceManager;
    private final Font font;
    private final int fontTextureId;

    public Renderer(long window, int screenWidth, int screenHeight, ResourceManager resourceManager) {
        this.window = window;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resourceManager = resourceManager;
        this.fontTextureId = glGenTextures();

        Font font1;
        try{
            font1 = Font.createFont(Font.TRUETYPE_FONT, new File("./resources/Font/kenvector_future.ttf"));

        }catch (IOException | FontFormatException e){
            e.printStackTrace();
            font1 = null;

        }
        this.font = font1;
    }
    private FontMetrics createFontMetrics(Font font){
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setFont(font);
        return g.getFontMetrics();
    }

    public void renderText(String text, float x, float y, int fontSize, Color color){

        Font textFont = this.font.deriveFont((float)fontSize);
        FontMetrics fontMetrics = createFontMetrics(textFont);

        int width = fontMetrics.stringWidth(text);
        int height = fontMetrics.getHeight();
        BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2d = image.createGraphics();
        g2d.setFont(textFont);
        g2d.setColor(color);
        g2d.drawString(text, 0, height - fontMetrics.getDescent());

        byte[] data = ((DataBufferByte) image.getData().getDataBuffer()).getData();
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        this.resourceManager.generateTexture(this.fontTextureId, image.getWidth(), image.getHeight(), buffer);

        float [] transMatrix = this.getTransformationMatrix(x + width, y, width, height, 0);
        this.renderTexture(transMatrix);
    }
    public void renderHealth(int hp){
        final int height    = 30;
        final int y         = Game.SCREEN_HEIGHT - height;
        final int width     = 30;
        int x               = Game.SCREEN_WIDTH / 2 - hp * (width + 10) / 2;

        Texture texture = this.resourceManager.textures.get(11);
        glBindTexture(GL_TEXTURE_2D, texture.textureId);
        glBindVertexArray(texture.vertexArrayId);

        for(int i = 0; i < hp; i++, x += width + 10){
            float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
            this.renderTexture(transMatrix);
        }


    }
    private void renderTexture(float [] transMatrix){

        int programId = this.resourceManager.programs.get(0);
        glUseProgram(programId);
        int loc = glGetUniformLocation(programId, "transMatrix");
        if(loc == -1){
            System.out.println("Failed to get location of transMatrix");
            glfwSetWindowShouldClose(this.window, true);
            System.exit(1);
        }
        glUniformMatrix3fv(loc, true, transMatrix);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    private float[] matMul(float [] m0, float[]m1){
        float [] res = new float[9];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3;j++){
                for(int k = 0; k < 3; k++){
                    res[i + j * 3] += m0[i + k * 3] * m1[k + j * 3];
                }
            }
        }
        return res;
    }

    public float[] getTransformationMatrix(float x, float y, float width, float height, float rotation){
        float r = (float) (rotation * Math.PI / 180.0f);
        float [] rotationM = new float[]{
                (float)Math.cos(r), (float)Math.sin(r), 0,
                (float)-Math.sin(r), (float)Math.cos(r), 0,
                0,0,1
        };


        float transformX = ((x + this.screenWidth) / (float)this.screenWidth) - 1.0f;
        float transformY = ((y + this.screenHeight) / (float)this.screenHeight) - 1.0f;
        float [] translationM = new float[]{
                1,0,0,
                0,1,0,
                transformX, transformY, 1
        };

        float scaleX = ((width + this.screenWidth) / (float)this.screenWidth) - 1.0f;
        float scaleY = ((height + this.screenHeight) / (float)this.screenHeight) - 1.0f;

        float [] scaleM = new float[]{
             scaleX, 0, 0,
             0, scaleY, 0,
             0,  0,1
        };

        float [] m0 = this.matMul(translationM, rotationM);
        return this.matMul(m0, scaleM);
    }


    public void renderEntity(Entity entity){
        Texture texture = this.resourceManager.textures.get(entity.getTextureIdx());

        int programId = this.resourceManager.programs.get(0);
        glUseProgram(programId);
        glBindTexture(GL_TEXTURE_2D, texture.textureId);
        glBindVertexArray(texture.vertexArrayId);

        float [] transMatrix = this.getTransformationMatrix(entity.x, entity.y,entity.width, entity.height, entity.getRotation());

        this.renderTexture(transMatrix);
    }





    public void renderEntities(final List<? extends Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
