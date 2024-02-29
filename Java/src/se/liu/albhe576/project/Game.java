package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL40.*;

import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Game
{
    private long window;

    private void initGLFW(){

        final String title ="Jalaga";

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        this.window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, title, NULL, NULL);
        if(window == NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }


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
    private final ArrayList<Entity> entities;
    private final Renderer renderer;
    public static final int SCREEN_WIDTH = 620;
    public static final int SCREEN_HEIGHT = 480;
    private final InputState inputState;



    public Game() throws IOException {
        this.initGLFW();
        this.entities = GameData.loadEntities();
        this.renderer = new Renderer(this.window);
        this.inputState = new InputState(this.window);
    }


    private Player getPlayer(){
        return (Player) this.entities.get(0);
    }


    private void updateEntities(long startTime){
        for(int i = 0; i < entities.size(); i++){
            this.entities.get(i).update(startTime);
        }
    }

    private void checkCollision(){
        List<Entity> bullets =  this.entities.stream().filter(s -> s instanceof Bullet).toList();
        List<Entity> rest = this.entities.stream().filter(s -> !(s instanceof Bullet)).toList();
        for(Entity bulletEntity  : bullets){
            Bullet bullet = (Bullet) bulletEntity;
            bullet.checkCollision(rest);
        }
    }

    public void runGame(){

        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            this.renderer.renderEntities(this.entities);
            glfwSwapBuffers(window);
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
