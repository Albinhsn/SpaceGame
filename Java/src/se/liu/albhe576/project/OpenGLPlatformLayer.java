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
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OpenGLPlatformLayer extends PlatformLayer
{
    private long window;
    private ArrayList<Entity> entities;

    private int program;
    private int vertexArrayId;
    private int lineProgram;
    private int lineVertexArrayId;

    private int lineBufferId;
    private boolean drawCall;

    private int bufferId;
    private int lineIndexBufferId;

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
    private void initLineTextureProgram(){
        int vShader = createAndCompileShader("./shaders/white_line.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/white_line.ps", GL_FRAGMENT_SHADER);

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

        this.lineBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.lineBufferId);

        glEnableVertexAttribArray(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(3), 0);

        this.lineIndexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.lineIndexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);
    }


    private void initQuadTextureProgram(){
        // ToDo error handle errors compiling shaders
        int vShader = createAndCompileShader("./shaders/texture.vs", GL_VERTEX_SHADER);
        int fShader = createAndCompileShader("./shaders/texture.ps", GL_FRAGMENT_SHADER);

        this.program = glCreateProgram();
        glAttachShader(this.program, vShader);
        glAttachShader(this.program, fShader);

        glBindAttribLocation(this.program, 0, "inputPosition");
        glBindAttribLocation(this.program, 1, "inputTexCoord");

        glLinkProgram(this.program);

        IntBuffer status = BufferUtils.createIntBuffer(1);
        glGetProgramiv(this.program, GL_LINK_STATUS, status);
        if(status.get(0) != 1){
            String infoLog = glGetProgramInfoLog(program);
            System.out.println(infoLog);
            System.exit(1);
        }

        glUseProgram(this.program);

        int []indicies = new int[]{0,1,2,1,3,2};

        this.vertexArrayId = glGenVertexArrays();
        glBindVertexArray(this.vertexArrayId);

        this.bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.bufferId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, sizeofFloatArray(5), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, sizeofFloatArray(5), sizeofFloatArray(3));

        int indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);
    }

    public void run(){
        this.init();
        this.initQuadTextureProgram();
        this.initLineTextureProgram();
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
        glUseProgram(this.program);
        glBindVertexArray(this.vertexArrayId);
        glBindBuffer(GL_ARRAY_BUFFER, this.bufferId);
        ByteBuffer mapBuffer = Bounds.getBoundsBuffer(128, 128, Color.GREEN, 1);
        float width = 0.8f;
        float height = 0.8f;
        float [] mapBufferData = new float[]{
                -width, -height, 0.0f, 0.0f, 1.0f,
                width, -height, 0.0f,  1.0f, 1.0f,
                -width, height, 0.0f,  0.0f, 0.0f,
                width, height, 0.0f,   1.0f, 0.0f
        };
        this.renderQuadTexture(128, 128, mapBuffer, mapBufferData);

    }

    public void renderQuadTexture(int width, int height, ByteBuffer byteBuffer, float[] bufferData){

        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, bufferData, GL_STATIC_DRAW);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);

        glGenerateMipmap(GL_TEXTURE_2D);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    }

    public void renderEntity(Entity entity){
        float [] bufferData = this.getBufferData(entity);
        Texture texture = entity.getTexture();
        ByteBuffer byteBuffer = texture.getData();

        // Render entity texture
        renderQuadTexture(texture.getWidth(), texture.getHeight(), byteBuffer, bufferData);

        Bounds bounds = entity.getBounds();
        float [] boundsBufferData = this.getBoundsBufferData(entity.x, entity.y, bounds);
        renderQuadTexture(texture.getWidth(), texture.getHeight(), entity.getBounds().getByteBuffer(), boundsBufferData);

    }

    @Override public void drawLines(List<ScreenPoint> points){
        this.drawCall = true;
        this.screenPoints =points;
    }
    private List<ScreenPoint> screenPoints;

    public void drawLines(){
        glUseProgram(this.lineProgram);
        glBindVertexArray(this.lineVertexArrayId);

        glBindBuffer(GL_ARRAY_BUFFER, this.lineBufferId);
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
        glUseProgram(this.program);
        glBindVertexArray(this.vertexArrayId);
        glBindBuffer(GL_ARRAY_BUFFER, this.bufferId);
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

    public OpenGLPlatformLayer(int width, int height, boolean editor){
        super(width, height, editor);
    }

    @Override public void drawEntities(final ArrayList<Entity> entities) {
        this.drawCall = true;
        this.entities = entities;
    }
}
