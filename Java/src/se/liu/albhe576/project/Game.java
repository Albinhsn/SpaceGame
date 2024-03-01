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
    private ArrayList<Bullet> bullets;
    private Wave wave;
    private final Renderer renderer;
    public static final int SCREEN_WIDTH = 620;
    public static final int SCREEN_HEIGHT = 480;
    private final InputState inputState;
    private final ResourceManager resourceManager;
    private final Player player;

    private final Background background;

    public Game() {
        this.initGLFW();
        this.resourceManager = new ResourceManager();

        this.player   = this.resourceManager.getPlayer();
        this.bullets = new ArrayList<>();

        this.renderer = new Renderer(this.window, SCREEN_WIDTH, SCREEN_HEIGHT, resourceManager);
        this.background = new Background();
        this.inputState = new InputState(this.window);
        this.wave = this.resourceManager.getWave(0);
    }


    // ToDo figure out if this always is just bullets
    private void updateEntities(long startTime){
        for (Entity entity : bullets) {
            entity.update(startTime);
        }
    }

    private void checkCollision(){
        boolean collided     = false;
        List<Bullet> bullets =  this.bullets;
        List<Entity> entities= this.wave.getEnemies();
        entities.add(this.player);

        for(Bullet bullet: bullets){
            collided = bullet.checkCollision(entities);
        }
        if(collided){
            this.bullets = (ArrayList<Bullet>) this.bullets.stream().filter(bullet -> bullet.alive).collect(Collectors.toList());
            this.wave.removeKilledEnemies();
        }
    }
    public void updatePlayer(){
        player.updatePlayerAcceleration(this.inputState);

        if(this.inputState.isSpacePressed()){
            boolean shot =  this.player.shoot();
            if(shot){
                this.bullets.add(resourceManager.createNewBullet(this.player));
            }
        }
    }

    public void runGame(){

        final long startTime = System.currentTimeMillis();

        while(!glfwWindowShouldClose(window)){
            // Poll input events
            glfwPollEvents();

            // Update entities
            this.updatePlayer();
            this.updateEntities(startTime);
            this.wave.updateWave(startTime);
            this.checkCollision();
            if(!this.player.alive){
                System.out.println("Game Over!");
                System.exit(1);
            }

            // Init new frame
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            // Render entities
            this.renderer.renderEntity(this.player);
            this.renderer.renderEntities(this.bullets);
            this.renderer.renderEntities(this.wave.getEnemies());

            // Update background :)
            this.background.update(startTime);
            this.renderer.renderEntities(this.background.getMeteors());

            // This is the actual draw call
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
