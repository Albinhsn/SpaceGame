package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL40.*;

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
    private long score;

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
    private List<Bullet> bullets;
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
        this.score = 0;
        this.resourceManager = new ResourceManager();

        this.player   = this.resourceManager.getPlayer();
        this.bullets = new ArrayList<>();

        this.renderer = new Renderer(this.window, SCREEN_WIDTH, SCREEN_HEIGHT, resourceManager);
        this.background = new Background();
        this.inputState = new InputState(this.window);
        this.wave = this.resourceManager.getWave(0);
    }


    // ToDo figure out if this always is just bullets
    private void updateBullets(){
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    private void checkCollision(){

        List<Entity> entities= this.wave.getEnemies();
        entities.add(this.player);

        for(Bullet bullet: this.bullets){
            if(bullet.checkCollision(entities) && bullet.parent == this.player){
                this.score += 100;
                System.out.println(this.score);
            }
        }

        this.bullets.removeIf(entity -> !entity.alive);
        this.wave.removeKilledEnemies();
    }
    public void updatePlayer(){
        if(player.updatePlayer(this.inputState)){
            Bullet bullet = this.resourceManager.createNewBullet(this.player);
            this.bullets.add(bullet);
        }
    }

    public void runGame(){

        final long startTime = System.currentTimeMillis();

        while(!glfwWindowShouldClose(window)){
            // Poll input events
            glfwPollEvents();

            // Update entities
            this.updatePlayer();
            this.updateBullets();
            this.bullets.addAll(this.wave.updateWave(startTime, this.resourceManager));

            this.checkCollision();
            if(!this.player.alive){
                System.out.printf("Game Over!\nScore: %d\n", this.score);
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
            this.background.update();
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
