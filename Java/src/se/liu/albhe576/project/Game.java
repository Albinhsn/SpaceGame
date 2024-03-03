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
    private long score;
    private Timer timer;
    private long lastUpdated;

    private final List<Bullet> bullets;
    private final Wave wave;
    private final Renderer renderer;
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;
    private final InputState inputState;
    private final ResourceManager resourceManager;
    private final Player player;
    private final Background background;

    private void updateBullets(){
        for (Bullet bullet : bullets) {
            bullet.update(this.timer.getLastTick());
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
        if(player.updatePlayer(this.inputState, this.timer.getLastTick())){
            this.bullets.add(this.resourceManager.createNewBullet(this.player));
        }
    }

    private boolean shouldHandleUpdates(){
        long lastTick = this.timer.getLastTick();
        // 16 ms = 60fps
        if(lastTick >= this.lastUpdated + 16){
            this.lastUpdated = lastTick;
            return true;
        }
        return false;
    }

    private void gameLoop(){
        // Update entities
        this.timer.updateTimer();


        if(this.shouldHandleUpdates()){
            // We just poll the events when we want to handle updates
            glfwPollEvents();
            this.updatePlayer();
            this.bullets.addAll(this.wave.updateWave(this.timer.getLastTick(), this.resourceManager));
            this.updateBullets();
            this.checkCollision();
            if(!this.player.alive){
                System.out.printf("Game Over!\nScore: %d\n", this.score);
                System.exit(1);
            }

            this.background.update();
        }

        // Init new frame
        this.initNewFrame();

        // Render entities
        this.renderEverything();

    }
    private void renderEverything(){
        List<Entity> entities = this.wave.getEnemies();
        entities.add(this.player);
        entities.addAll(this.bullets);
        entities.addAll(this.background.getMeteors());

        this.renderer.renderEntities(entities);
        this.renderer.renderText(String.format("Score: %d", this.score), -Game.SCREEN_WIDTH, SCREEN_HEIGHT - 15, 15, Color.WHITE);
        this.renderer.renderHealth(this.player.hp);
    }

    private void initNewFrame() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    public void runGame(){

        this.timer.startTimer();
        while(!glfwWindowShouldClose(window)){
            // Poll input events

            this.gameLoop();

            // This is the actual draw call
            glfwSwapBuffers(window);
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
    }
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
        glfwSwapInterval(0);

        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public Game() {
        this.initGLFW();
        this.timer = new Timer();
        this.score = 0;
        this.lastUpdated = 0;
        this.resourceManager = new ResourceManager();

        this.player   = this.resourceManager.getPlayer();
        this.bullets = new ArrayList<>();

        this.renderer = new Renderer(this.window, SCREEN_WIDTH, SCREEN_HEIGHT, resourceManager);
        this.background = new Background();
        this.inputState = new InputState(this.window);
        this.wave = this.resourceManager.getWave(0);
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
