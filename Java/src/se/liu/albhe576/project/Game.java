package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL40.*;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

            assert vidmode != null;
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
    private final ResourceManager resourceManager;
    private final Player player;

    private final Background background;

    public Game() throws IOException {
        this.initGLFW();
        this.resourceManager = new ResourceManager();

        this.player   = this.resourceManager.getPlayer();
        this.entities = new ArrayList<>();

        this.renderer = new Renderer(this.window, SCREEN_WIDTH, SCREEN_HEIGHT, resourceManager);
        this.background = new Background();
        this.inputState = new InputState(this.window);
    }


    private void updateEntities(long startTime){
        for (Entity entity : entities) {
            entity.update(startTime);
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
    public void updatePlayer(){
        player.updatePlayerAcceleration(this.inputState);

        if(this.inputState.isSpacePressed()){
            boolean shot =  this.player.shoot();
            if(shot){
                this.entities.add(resourceManager.createNewBullet(this.player));
            }
        }
    }

    public void runGame(){

        final long startTime = System.currentTimeMillis();

        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();

            this.updatePlayer();
            this.updateEntities(startTime);
            this.background.updateBackground(startTime);

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            //this.renderer.renderEntity(this.player);
            //this.renderer.renderEntities(this.entities);
            this.renderer.renderEntities(this.background.getMeteors());
            System.out.println("---");


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
