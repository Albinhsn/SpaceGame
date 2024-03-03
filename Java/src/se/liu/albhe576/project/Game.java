package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL40.*;

import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
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
    private UI ui;
    private Map<UIState, UI> uiMap;
    private UIState uiState;

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
            this.queryInput();
            if(this.inputState.isPPressed()){
                this.updateUIState(UIState.PAUSE_MENU);
            }

            // Handle updates
            this.updatePlayer();
            this.bullets.addAll(this.wave.updateWave(this.timer.getLastTick(), this.resourceManager));
            this.updateBullets();
            this.checkCollision();

            this.background.update();
        }

        // Render entities
        this.renderEverything();
    }

    private void renderEverything(){
        List<Entity> entities = this.wave.getEnemies();
        entities.add(this.player);
        entities.addAll(this.bullets);

        this.renderer.renderEntities(entities);
        this.renderer.renderText(String.format("Score: %d", this.score), -Game.SCREEN_WIDTH, SCREEN_HEIGHT - 15, 15, Color.WHITE);
        this.renderer.renderHealth(this.player.hp);
    }

    private void initNewFrame() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    private void queryInput(){
        glfwPollEvents();
        this.inputState.handleMouseInput();
    }
    private void updateUIState(UIState newState){
        if(this.uiState == UIState.GAME_RUNNING && newState != UIState.GAME_RUNNING){
            this.timer.stopTimer();
        }else if(this.uiState != UIState.GAME_RUNNING && newState == UIState.GAME_RUNNING){
            this.timer.startTimer();
        }else if(this.uiState != UIState.SETTINGS_MENU && newState == UIState.SETTINGS_MENU){
            SettingsMenuUI settingsUI = (SettingsMenuUI)  this.uiMap.get(UIState.SETTINGS_MENU);
            settingsUI.setParentState(this.uiState);
        }

        this.uiState = newState;
    }

    public void runGame(){

        while(!glfwWindowShouldClose(window)){
            // Init new frame
            this.initNewFrame();
            this.background.updateAndRender(this.renderer);

            if (this.uiState == UIState.GAME_RUNNING) {
                this.gameLoop();
                if(!this.player.alive){
                    this.uiState = UIState.GAME_OVER_MENU;
                }
            }else{
                this.queryInput();
            }


           updateUIState(this.uiMap.get(this.uiState).render(this.inputState, this.renderer));

            glfwSwapBuffers(window);
            this.inputState.resetState();
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
        this.uiState = UIState.MAIN_MENU;
        this.initUIMap();
    }
    private void initUIMap(){
        this.uiMap = new HashMap<>();
                this.uiMap.put(UIState.MAIN_MENU, new MainMenuUI(this.window));
                this.uiMap.put(UIState.GAME_RUNNING, new GameRunningUI());
                this.uiMap.put(UIState.GAME_OVER_MENU, new GameOverUI());
                this.uiMap.put(UIState.PAUSE_MENU, new PauseMenuUI());
                this.uiMap.put(UIState.SETTINGS_MENU , new SettingsMenuUI());
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
