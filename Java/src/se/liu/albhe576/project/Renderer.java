package se.liu.albhe576.project;

import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer implements Runnable
{
    private List<Integer> textures;
    private List<Integer> programs;
    private long window;
    private List<Entity> entities;
    private boolean drawCall;

    public InputState inputState;
    private int screenHeight;
    private int screenWidth;

    public int getScreenWidth(){
        return screenWidth;
    }
    public InputState getInputState(){
        return this.inputState;
    }
    public int getScreenHeight(){
        return screenHeight;
    }

    public Renderer(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.inputState = new InputState();
    }
    public Thread runRenderer(){
        Thread platformThread = new Thread(this);
        platformThread.start();

        return platformThread;
    }

    private void renderEntity(Entity entity){
        glBindTexture(GL_TEXTURE_2D, entity.getTextureId());

        // Figure out what to bind in terms of transformation matrix

        // glDrawElements(GL_TRIANGLES, );
    }


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
        final String title ="Jalaga";

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(this.getScreenWidth(), this.getScreenHeight(), title, NULL, NULL);
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

    public void run(){
        this.init();

        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();
            this.handleMouseInput();
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
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

    public void drawEntities(final List<Entity> entities) {
        this.drawCall = true;
        this.entities = entities;
    }

}
