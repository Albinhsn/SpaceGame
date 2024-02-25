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
    private static class OpenGLTexture{
        public int program;
        public int vertexArrayId;
        public int vertexBufferId;
        public int vertexCount;
        public int indexBufferId;
        public int indexCount;
        public int textureId;

        public int textureUnit;

    }
    private final OpenGLTexture rectTexture;
    private final OpenGLTexture lineTexture;
    private final OpenGLTexture fontTexture;
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
        this.fontTexture.textureUnit = 0;
        glActiveTexture(GL_TEXTURE0 + this.fontTexture.textureUnit);
        this.fontTexture.textureId = glGenTextures();
        Texture texture = this.font.texture;

        glBindTexture(GL_TEXTURE_2D, this.fontTexture.textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getData());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);

        int vShader = createAndCompileShader("./shaders/font.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/font.ps", GL_FRAGMENT_SHADER);


        fontTexture.program = glCreateProgram();
        glAttachShader(fontTexture.program, vShader);
        glAttachShader(fontTexture.program, fShader);

        glBindAttribLocation(fontTexture.program, 0, "inputPosition");
        glBindAttribLocation(fontTexture.program, 1, "inputTexCoord");
        glLinkProgram(fontTexture.program);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(fontTexture.program, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(fontTexture.program);
            System.out.println(infoLog);
            System.exit(1);
        }

        glUseProgram(fontTexture.program);

        final int maxLength = 32 * 6;
        this.fontTexture.vertexCount = maxLength;
        this.fontTexture.indexCount = maxLength;

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

        fontTexture.indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, fontTexture.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);

        this.updateText("A", 32, 10,10);

    }
    private void initLineTextureProgram(){
        int vShader = createAndCompileShader("./shaders/white.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/white.ps", GL_FRAGMENT_SHADER);

        lineTexture.program = glCreateProgram();
        glAttachShader(lineTexture.program, vShader);
        glAttachShader(lineTexture.program, fShader);

        glBindAttribLocation(lineTexture.program, 0, "inputPosition");

        glLinkProgram(lineTexture.program);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(lineTexture.program, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(lineTexture.program);
            System.out.println(infoLog);
            System.exit(1);
        }

        glUseProgram(lineTexture.program);

        int []indicies = new int[]{0,1};
        lineTexture.vertexArrayId = glGenVertexArrays();
        glBindVertexArray(lineTexture.vertexArrayId);

        lineTexture.vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, lineTexture.vertexBufferId);

        glEnableVertexAttribArray(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(3), 0);

        lineTexture.indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, lineTexture.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);

        lineTexture.textureId = 0;
    }


    private void initRectangleTextureProgram(OpenGLTexture texture){
        int vShader = createAndCompileShader("./shaders/texture.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/texture.ps", GL_FRAGMENT_SHADER);

        texture.program = glCreateProgram();
        glAttachShader(texture.program, vShader);
        glAttachShader(texture.program, fShader);

        glBindAttribLocation(texture.program, 0, "inputPosition");
        glBindAttribLocation(texture.program, 1, "inputTexCoord");

        glLinkProgram(texture.program);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(texture.program, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(texture.program);
            System.out.println(infoLog);
            System.exit(1);
        }

        glUseProgram(texture.program);

        int []indicies = new int[]{0,1,2,1,3,2};

        texture.vertexArrayId = glGenVertexArrays();
        glBindVertexArray(texture.vertexArrayId);

        texture.vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texture.vertexBufferId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(5), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, sizeofFloatArray(5), sizeofFloatArray(3));

        texture.indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, texture.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);

    }


    public void run(){
        this.init();
        this.initRectangleTextureProgram(rectTexture);
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
        glUseProgram(rectTexture.program);
        glBindVertexArray(rectTexture.vertexArrayId);
        glBindBuffer(GL_ARRAY_BUFFER, rectTexture.vertexBufferId);
        ByteBuffer mapBuffer = Bounds.getBoundsBuffer(128, 128, Color.GREEN, 1);
        float width = 0.8f;
        float height = 0.8f;
        float [] mapBufferData = new float[]{
                -width, -height, 0.0f, 0.0f, 1.0f,
                width, -height, 0.0f,  1.0f, 1.0f,
                -width, height, 0.0f,  0.0f, 0.0f,
                width, height, 0.0f,   1.0f, 0.0f
        };
        this.renderQuadTexture(null, 128, 128, mapBuffer, mapBufferData, rectTexture.vertexBufferId, 6);

    }

    public void renderQuadTexture(OpenGLTexture texture, int width, int height, ByteBuffer byteBuffer, float[] bufferData, int bufferId, int vertices){

        glUseProgram(texture.program);
        glBindVertexArray(texture.vertexArrayId);

        glActiveTexture(texture.textureId);
        glBindTexture(GL_TEXTURE_2D,  texture.textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);

        glGenerateMipmap(GL_TEXTURE_2D);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

    }

    public void renderEntity(Entity entity){
        float [] bufferData = this.getBufferData(entity);
        Texture texture = entity.getTexture();
        // Render entity texture
        renderQuadTexture(null, texture.getWidth(), texture.getHeight(), texture.getData(), bufferData, rectTexture.vertexBufferId, 6);

        Bounds bounds = entity.getBounds();
        float [] boundsBufferData = this.getBoundsBufferData(entity.x, entity.y, bounds);
        renderQuadTexture(null, texture.getWidth(), texture.getHeight(), entity.getBounds().getByteBuffer(), boundsBufferData, rectTexture.vertexBufferId, 6);

    }

    @Override public void drawLines(List<ScreenPoint> points){
        this.drawCall = true;
        this.screenPoints =points;
    }

    public void drawLines(){
        glUseProgram(lineTexture.program);
        glBindVertexArray(lineTexture.vertexArrayId);

        glBindBuffer(GL_ARRAY_BUFFER, lineTexture.vertexBufferId);
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
        glUseProgram(this.rectTexture.program);
        glBindVertexArray(this.rectTexture.vertexArrayId);
        glBindBuffer(GL_ARRAY_BUFFER, this.rectTexture.vertexBufferId);
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
        glUseProgram(this.fontTexture.program);
        int location = glGetUniformLocation(this.fontTexture.program, "pixelColor");
        if(location == -1){
            System.out.println("Unable to find pixelColor location for fontShader");
            System.exit(1);
        }
        glUniform4fv(location, pixelColor);

        location = glGetUniformLocation(this.fontTexture.program, "shaderTexture");
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
