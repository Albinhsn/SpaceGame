package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OpenGLGraphicsLayer extends GraphicsLayer
{
    private int textureProgram;
    private final int MAX_TEXT_LENGTH = 32;
    private int textProgram;
    private int textTextureId;
    private int lineProgram;
    private int lineVertexArrayId;
    private int lineVertexBufferId;
    private int lineIndexBufferId;
    private static class OpenGLTexture{
        public int vertexArrayId;
        public int vertexBufferId;
        public int vertexCount;
        public int indexBufferId;
        public int indexCount;
        public int textureId;

        public int textureUnit;

    }
    private Font font;
    private List<ScreenPoint> screenPoints;
    private long window;
    private List<Entity> entities;

    private boolean drawCall;

    private void initInputHandling(){
        glfwSetMouseButtonCallback(window,(window2, button, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_RELEASE){
                boolean mousePressed = action == GLFW_PRESS;
		switch(button){
                    case GLFW_MOUSE_BUTTON_LEFT:{
			this.inputState.setMouse1(mousePressed);
                        break;
                    }
                    case GLFW_MOUSE_BUTTON_RIGHT:{
                        this.inputState.setMouse2(mousePressed);
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }
        });

        glfwSetKeyCallback(window, (window2, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true);
            }
            else if(action == GLFW_PRESS || action == GLFW_RELEASE){
                boolean pressed = action == GLFW_PRESS;
                switch(key){
                    case GLFW_KEY_W:{
                        this.inputState.setW(pressed);
                        break;
                    }
                    case GLFW_KEY_A:{
                        this.inputState.setA(pressed);
                        break;
                    }
                    case GLFW_KEY_S:{
                        this.inputState.setS(pressed);
                        break;
                    }
                    case GLFW_KEY_D:{
                        this.inputState.setD(pressed);
                        break;
                    }
                    case GLFW_KEY_SPACE:{
                        this.inputState.setSpace(pressed);
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }
        });
    }

    private void init(){
        final String title ="Space Invaders";

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(this.getWidth(), this.getHeight(), title, NULL, NULL);
        if(window == NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        this.initInputHandling();

        try(MemoryStack stack = stackPush()){
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);

        GL.createCapabilities();
    }

    private String getShaderSource(String fileLocation) throws IOException{
        return Files.readString(Paths.get(fileLocation));
    }


    private int createAndCompileShader(String fileLocation, int shaderType){
        String source = null;
        try{
            source = getShaderSource(fileLocation);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);

        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        glGetShaderiv(shader, GL_COMPILE_STATUS, intBuffer);
        if(intBuffer.get(0) != 1){
            String log = glGetShaderInfoLog(shader);
            System.out.println("Error loading shader\n");
            System.out.println(log);
            System.exit(2);
        }

        return shader;
    }

    public int sizeofFloatArray(int capacity){
        return capacity * 4;
    }


    public float[] getBufferData(Entity entity){
        float width = entity.getTextureWidth();
        float height = entity.getTextureHeight();

        float x = Game.convertIntSpaceToFloatSpace(entity.x);
        float y = Game.convertIntSpaceToFloatSpace(entity.y);

        return new float[]{
            -width + x, -height - y, 0.0f, 0.0f, 1.0f,
            width + x, -height - y, 0.0f,  1.0f, 1.0f,
            -width + x, height - y, 0.0f,  0.0f, 0.0f,
            width + x, height - y, 0.0f,   1.0f, 0.0f
        };
    }
    private float[] getBoundsBufferData(int x, int y, Bounds bounds){
        float width = bounds.getWidth();
        float height = bounds.getHeight();


        float yOffset = Game.convertIntSpaceToFloatSpace(-y) + bounds.getTextureOffsetY();
        float xOffset = Game.convertIntSpaceToFloatSpace(x) + bounds.getTextureOffsetX();

        return new float[]{
                -width + xOffset, -height + yOffset, 0.0f, 0.0f, 1.0f,
                width + xOffset, -height + yOffset, 0.0f,  1.0f, 1.0f,
                -width + xOffset, height + yOffset, 0.0f,  0.0f, 0.0f,
                width + xOffset, height + yOffset, 0.0f,   1.0f, 0.0f
        };

    }
    private void initTextProgram(){
        Texture texture = this.font.texture;

        this.textTextureId = glGenTextures();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.textTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getData());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);

        int vShader = createAndCompileShader("./shaders/font.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/font.ps", GL_FRAGMENT_SHADER);


        this.textProgram = glCreateProgram();
        glAttachShader(this.textProgram, vShader);
        glAttachShader(this.textProgram, fShader);

        glBindAttribLocation(this.textProgram, 0, "inputPosition");
        glBindAttribLocation(this.textProgram, 1, "inputTexCoord");
        glLinkProgram(this.textProgram);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(this.textProgram, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(this.textProgram);
            System.out.println(infoLog);
            System.exit(1);
        }

        this.fontTexture.vertexCount = MAX_TEXT_LENGTH;
        this.fontTexture.indexCount = MAX_TEXT_LENGTH;

        float [] vertices = new float[this.fontTexture.vertexCount];
        Arrays.fill(vertices, 0);

        int [] indicies = new int[this.fontTexture.indexCount];
        Arrays.fill(indicies, 0);

        fontTexture.vertexArrayId = glGenVertexArrays();
        glBindVertexArray(fontTexture.vertexArrayId);

        fontTexture.vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, fontTexture.vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(3), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, sizeofFloatArray(5), sizeofFloatArray(3));

        int indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);

        this.updateText("A", 32, 10,10);

    }
    private void initLineTextureProgram(){
        int vShader = createAndCompileShader("./shaders/white.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/white.ps", GL_FRAGMENT_SHADER);

        this.lineProgram = glCreateProgram();
        glAttachShader(this.lineProgram, vShader);
        glAttachShader(this.lineProgram, fShader);

        glBindAttribLocation(this.lineProgram, 0, "inputPosition");

        glLinkProgram(this.lineProgram);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(this.lineProgram, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(this.lineProgram);
            System.out.println(infoLog);
            System.exit(1);
        }

        glUseProgram(this.lineProgram);

        int []indicies = new int[]{0,1};
        this.lineVertexArrayId = glGenVertexArrays();
        glBindVertexArray(this.lineVertexArrayId);

        this.lineVertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.lineVertexBufferId);

        glEnableVertexAttribArray(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(3), 0);

        this.lineIndexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.lineIndexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_DYNAMIC_DRAW);

    }




    private void initRectangleTextureProgram(){
        int vShader = createAndCompileShader("./shaders/texture.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/texture.ps", GL_FRAGMENT_SHADER);

        this.textureProgram = glCreateProgram();
        glAttachShader(this.textureProgram, vShader);
        glAttachShader(this.textureProgram, fShader);

        glBindAttribLocation(this.textureProgram, 0, "inputPosition");
        glBindAttribLocation(this.textureProgram, 1, "inputTexCoord");

        glLinkProgram(this.textureProgram);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(this.textureProgram, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(this.textureProgram);
            System.out.println(infoLog);
            System.exit(1);
        }
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(5), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, sizeofFloatArray(5), sizeofFloatArray(3));


        int []indices = new int[]{0,1,2,1,3,2};

        int indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    }


    public void run(){
        this.init();
        this.initRectangleTextureProgram();
        this.initLineTextureProgram();
        this.initTextProgram();
        if(this.editor){
            this.editorLoop();
        }else{
            this.gameLoop();
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
    }

    public void renderMapEdge(){
        // This belongs in the editor
        OpenGLTexture texture;
        glUseProgram(this.textureProgram);
        glBindVertexArray(texture.vertexArrayId);
        glBindBuffer(GL_ARRAY_BUFFER, texture.vertexBufferId);

        ByteBuffer mapBuffer = Bounds.getBoundsBuffer(128, 128, Color.GREEN, 1);

        final float width = 0.8f;
        final float height = 0.8f;
        float [] edgeBufferData = new float[]{
                -width, -height, 0.0f, 0.0f, 1.0f,
                width, -height, 0.0f,  1.0f, 1.0f,
                -width, height, 0.0f,  0.0f, 0.0f,
                width, height, 0.0f,   1.0f, 0.0f
        };
        glBufferData(GL_ARRAY_BUFFER, edgeBufferData, GL_STATIC_DRAW);
        this.renderRectangleTexture(texture.vertexArrayId, 128, 128, mapBuffer);

    }

    public void renderRectangleTexture(int vertexArrayId, int width, int height, ByteBuffer byteBuffer){
        final int vertices = 6;

        glBindVertexArray(vertexArrayId);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);

        glGenerateMipmap(GL_TEXTURE_2D);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);

        glBindVertexArray(6);

    }

    public void renderEntity(Entity entity){
        float [] bufferData = this.getBufferData(entity);
        Texture texture = entity.getTexture();
        // Render entity texture
        renderRectangleTexture(texture.getWidth(), texture.getHeight(), texture.getData(), bufferData, rectTexture.vertexBufferId, 6);

        Bounds bounds = entity.getBounds();
        float [] boundsBufferData = this.getBoundsBufferData(entity.x, entity.y, bounds);
        renderRectangleTexture(texture.getWidth(), texture.getHeight(), entity.getBounds().getByteBuffer(), boundsBufferData, rectTexture.vertexBufferId, 6);

    }

    @Override public void drawLines(List<ScreenPoint> points){
        this.drawCall = true;
        this.screenPoints =points;
    }

    private void drawLines(){
        // ToDo optimize this to just be one draw call
        glUseProgram(this.lineProgram);
        glBindVertexArray(this.lineVertexArrayId);

        glBindBuffer(GL_ARRAY_BUFFER, this.lineVertexBufferId);
        for(int i = 0; i < this.screenPoints.size() - 1; i++){
            ScreenPoint start = this.screenPoints.get(i);
            ScreenPoint end = this.screenPoints.get(i+1);

            float startX = Game.convertIntSpaceToFloatSpace(start.x);
            float startY = Game.convertIntSpaceToFloatSpace(start.y);

            float endX = Game.convertIntSpaceToFloatSpace(end.x);
            float endY = Game.convertIntSpaceToFloatSpace(end.y);

            float[]buffer = new float[]{
                    startX, startY, 0.0f,
                    endX, endY, 0.0f
            };

            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glDrawElements(GL_LINES, 2, GL_UNSIGNED_INT, 0);
        }


    }

    public void drawEntities(){
        glUseProgram(this.textureProgram);
        for(int i = 0; i< this.entities.size(); i++){
            this.renderEntity(this.entities.get(i));
        }
    }
    private void handleMouseInput(){
        DoubleBuffer posBufferX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posBufferY= BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(this.window, posBufferX, posBufferY);

        // This is in range from 0 - ScreenWidth/ScreenHeight
        int posX = -Game.SCREEN_WIDTH + (int) posBufferX.get(0) * 2;
        int posY = -1 * (-Game.SCREEN_HEIGHT + (int) posBufferY.get(0) * 2);

        this.inputState.setMousePosition(posX, posY);
    }
    private void editorLoop(){

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.renderMapEdge();
        glfwSwapBuffers(window);
        while(!glfwWindowShouldClose(this.window)){
            if(this.drawCall){
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                this.drawLines();
                this.renderMapEdge();
                this.drawCall = false;
                glfwSwapBuffers(window);
            }
            drawText("A", 50, 0,0, Color.GREEN);


            glfwPollEvents();
            this.handleMouseInput();
        }

    }
    private void gameLoop(){

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        while(!glfwWindowShouldClose(this.window)){
            if(this.drawCall){
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                this.drawEntities();
                this.drawCall = false;
                glfwSwapBuffers(window);
            }


            glfwPollEvents();
            this.handleMouseInput();
        }

    }

    private List<float[]> buildStringVertexBuffer(String text, int fontSize, int x, int y){
        List<float[]> vertexBufferList = new ArrayList<>();

        float drawX = Game.convertIntSpaceToFloatSpace(x);
        float drawY = Game.convertIntSpaceToFloatSpace(y);
        float fontSizeF=  Game.convertIntSpaceToFloatSpace(fontSize);

        // Update the buffer
        int size = 0;
        float spaceSize = Game.convertIntSpaceToFloatSpace(this.font.spaceSize);
        for(int textIdx = 0; textIdx < text.length(); textIdx++){
            int letterIdx =  ((int)text.charAt(textIdx)) - 32;
            if(letterIdx == 0){
                drawX += spaceSize;
                continue;
            }
            Letter letter = this.font.getLetterByIndex(letterIdx);

            float letterSizeF32 = Game.convertIntSpaceToFloatSpace(letter.size);


            float [] charVertexBuffer = new float[]{
                    drawX,                    drawY,                    0.0f,          letter.left,          0.0f,
                    drawX + letterSizeF32,    drawY - fontSizeF,         0.0f,          letter.right,         1.0f,
                    drawX,                    drawY - fontSizeF,         0.0f,          letter.left,          1.0f,
                    drawX,                    drawY,                    0.0f,          letter.left,          0.0f,
                    drawX + letterSizeF32,    drawY,                    0.0f,          letter.right,         0.0f,
                    drawX + letterSizeF32,    drawY - fontSize,         0.0f,          letter.right,         1.0f
            };

            drawX += letter.size + 1.0f;
            vertexBufferList.add(charVertexBuffer);
            size += 30;
        }

        return vertexBufferList;

    }


    private void updateText(final String text, final int fontSize, int x, int y){

        glBindVertexArray(this.fontTexture.vertexArrayId);

        List<float[]> vertexBufferList = this.buildStringVertexBuffer(text, fontSize, x, y);
        ByteBuffer byteBuffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);
        for(int i = 0; i < vertexBufferList.size(); i++){
            float[] vbo = vertexBufferList.get(i);

            for(int j = 0; j < vbo.length; j++){
                byteBuffer.putFloat(i * vbo.length + j, vbo[j]);
            }
        }

        glUnmapBuffer(GL_ARRAY_BUFFER);

    }
    private void setTexture(int textureId, int textureUnit){
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
    private void setFontShaderParams(Color color){
        // Set shader param
        float[] pixelColor = new float[]{  color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f};
        glUseProgram(this.textProgram);
        int location = glGetUniformLocation(this.this.textProgram, "pixelColor");
        if(location == -1){
            System.out.println("Unable to find pixelColor location for fontShader");
            System.exit(1);
        }
        glUniform4fv(location, pixelColor);

        location = glGetUniformLocation(this.this.textProgram, "shaderTexture");
        if(location == -1){
            System.out.println("Unable to find shaderTexture location for fontShader");
            System.exit(1);
        }
        glUniform1i(location, 0);

    }
    @Override public void drawText(final String text, final int fontSize, final int x, final int y, final Color color) {

        this.updateText(text, fontSize, x, y);
        glBindVertexArray(this.fontTexture.vertexArrayId);

        setFontShaderParams(color);
        this.setTexture(this.fontTexture.textureId, this.fontTexture.textureUnit);

        glDrawElements(GL_TRIANGLES, this.fontTexture.indexCount, GL_UNSIGNED_INT, 0);

    }


    @Override public void drawEntities(final List<Entity> entities) {
        this.drawCall = true;
        this.entities = entities;
    }

    public OpenGLGraphicsLayer(int width, int height, boolean editor){
	super(width, height, editor);
        this.rectTexture = new OpenGLTexture();
        this.lineTexture = new OpenGLTexture();
        this.fontTexture = new OpenGLTexture();

        this.font = null;
        try{
	    this.font = Font.parseFontFromFile(GameData.getFontInfoLocation(0), GameData.getFontFileLocation(0), 32.0f);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Failed to parse font from " + GameData.getFontInfoLocation(0));
            System.exit(1);
        }


	this.screenPoints = null;
        this.entities = null;
    }
}
