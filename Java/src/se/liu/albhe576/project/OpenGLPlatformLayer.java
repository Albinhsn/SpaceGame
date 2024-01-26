package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    private boolean drawCall;

    private int bufferId;

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

        glfwSetKeyCallback(window, (window2, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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

        float x = entity.x;
        float y = entity.y;

        return new float[]{
            -width + x, -height - y, 0.0f, 0.0f, 1.0f,
            width + x, -height - y, 0.0f,  1.0f, 1.0f,
            -width + x, height - y, 0.0f,  0.0f, 0.0f,
            width + x, height - y, 0.0f,   1.0f, 0.0f
        };
    }
    private float[] getBoundsBufferData(float x, float y, Bounds bounds){
        float width = bounds.getWidth();
        float height = bounds.getHeight();

        float yOffset = bounds.getTextureOffsetY();

        return new float[]{
                -width + x, -height + yOffset - y, 0.0f, 0.0f, 1.0f,
                width + x, -height + yOffset - y, 0.0f,  1.0f, 1.0f,
                -width + x, height + yOffset - y, 0.0f,  0.0f, 0.0f,
                width +x, height + yOffset - y, 0.0f,   1.0f, 0.0f
        };

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

        this.loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
    }

    private void renderTexture(int width, int height, ByteBuffer byteBuffer, float[] bufferData){

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
        renderTexture(texture.getWidth(), texture.getHeight(), byteBuffer, bufferData);

        Bounds bounds = entity.getBounds();
        float [] boundsBufferData = this.getBoundsBufferData(entity.x, entity.y, bounds);
        // renderTexture(texture.getWidth(), texture.getHeight(), entity.getBounds().getByteBuffer(), boundsBufferData);

    }

    public void draw(){
        glUseProgram(this.program);
        glBindVertexArray(this.vertexArrayId);
        for(int i = 0; i< this.entities.size(); i++){
            this.renderEntity(this.entities.get(i));
        }
    }
    private void loop(){

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        while(!glfwWindowShouldClose(this.window)){
            if(this.drawCall){
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                this.draw();
                this.drawCall = false;
                glfwSwapBuffers(window);
            }


            glfwPollEvents();
        }

    }

    public OpenGLPlatformLayer(int width, int height){
        super(width, height);
    }

    @Override public void drawEntities(final ArrayList<Entity> entities) {
        this.drawCall = true;
        this.entities = entities;
    }
}
