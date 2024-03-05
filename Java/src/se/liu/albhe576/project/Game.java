package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL40.*;

import java.awt.*;
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
    private long prevTick;
    private long window;
    private int score;
    private final Timer timer;
    private long lastUpdated;
    private final List<Bullet> bullets;
    private Wave wave;
    private final Renderer renderer;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private final InputState inputState;
    private final ResourceManager resourceManager;
    private final Player player;
    private final Background background;
    private final Map<UIState, UI> uiMap;
    private UIState uiState;

    private void updateBullets(){
        this.bullets.removeIf(bullet -> !bullet.isWithinBounds());
        for (Bullet bullet : this.bullets) {
            bullet.update();
        }
    }

    private void checkCollision(){

        List<Entity> entities = this.wave.getEnemiesAsEntities();
        for(Bullet bullet: this.bullets){
            int score = bullet.checkCollision(entities);
            if(score != -1 && bullet.getParent() == this.player){
                this.score += score;
            }
            bullet.checkCollision(this.player);
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
        final long lastTick = this.timer.getLastTick();
        final int updateTimerMS = ResourceManager.STATE_VARIABLES.get("updateTimerMS").intValue();

        if(lastTick >= this.lastUpdated + updateTimerMS){
            this.lastUpdated = lastTick;
            return true;
        }
        return false;
    }


    private void gameLoop(){
        this.timer.updateTimer();

        if(this.shouldHandleUpdates()) {

            if(this.inputState.isPPressed()){
                this.updateUIState(UIState.PAUSE_MENU);
            }

            // Handle updates
            this.updatePlayer();

            List<Bullet> newEnemyBullets = this.wave.updateWave(this.timer.getLastTick(), this.resourceManager);
            this.bullets.addAll(newEnemyBullets);
            this.updateBullets();


            this.checkCollision();
        }

        this.renderGameEntities();
        this.wave.removeOutOfBoundsEnemies();
    }

    private void renderGameEntities(){
        this.renderer.renderEntities(this.wave.getEnemiesAsEntities());
        this.renderer.renderEntity(this.player);
        this.renderer.renderEntities(this.bullets);
    }

    private void initNewFrame() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    private void queryInputEvents(){
        glfwPollEvents();
        this.inputState.handleMouseInput();
    }
    private void resetGame(){
        this.score          = 0;
        this.lastUpdated    = 0;
        this.wave = this.resourceManager.getWave(0);
        this.bullets.clear();
        this.player.reset();
        this.timer.reset();
    }
    private void updateUIState(UIState newState){
        if(this.uiState == UIState.GAME_RUNNING && newState != UIState.GAME_RUNNING){
            this.timer.stopTimer();

        }else if(this.uiState != UIState.SETTINGS_MENU && newState == UIState.SETTINGS_MENU){
            // Get parent state for settings to know where we return back to
            SettingsMenuUI settingsUI = (SettingsMenuUI)  this.uiMap.get(UIState.SETTINGS_MENU);
            settingsUI.setParentState(this.uiState);

        }else if(this.uiState == UIState.GAME_OVER_MENU && newState != UIState.GAME_OVER_MENU){
            this.resetGame();
        }

        if(newState == UIState.GAME_RUNNING && !this.timer.isRunning()){
            this.timer.startTimer();
        }

        this.uiState = newState;
    }

    private void checkGameFinished(){
        if(!this.player.alive){
            this.uiState = UIState.GAME_OVER_MENU;
            ((GameOverUI) this.uiMap.get(UIState.GAME_OVER_MENU)).lostGame = true;
        }else if(this.wave.enemies().isEmpty()){
            this.uiState = UIState.GAME_OVER_MENU;
            ((GameOverUI) this.uiMap.get(UIState.GAME_OVER_MENU)).lostGame = false;
        }
    }
    private void renderInfoStrings(){
        long ms = System.currentTimeMillis()  - this.prevTick;
        this.renderer.renderTextStartAt(String.format("ms: %d\n", ms), -100.0f, 60.0f, 3.0f, Color.WHITE);
        this.renderer.renderTextStartAt(String.format("fps: %d\n", Math.min((int)(1000.0f/ms), 999)), -100.0f, 50.0f, 3.0f, Color.WHITE);
        this.prevTick = System.currentTimeMillis();
    }

    public void runGame(){

        while(!glfwWindowShouldClose(window)){
            this.initNewFrame();
            this.background.updateAndRender(this.renderer);

            this.inputState.resetState();
            this.queryInputEvents();


            if (this.uiState == UIState.GAME_RUNNING) {
                this.gameLoop();
                this.checkGameFinished();
            }

            this.renderInfoStrings();

            updateUIState(this.uiMap.get(this.uiState).render(this.inputState, this.renderer, this.window, this.score, this.player.hp));
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
        int vsync = this.resourceManager.STATE_VARIABLES.get("vsync").intValue();
        glfwSwapInterval(vsync);

        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0,0,Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    }
    public Game() {
        this.score              = 0;
        this.prevTick           = 0;
        this.lastUpdated        = 0;
        this.resourceManager    = new ResourceManager();

        Game.SCREEN_HEIGHT = this.resourceManager.STATE_VARIABLES.get("SCREEN_HEIGHT").intValue();
        Game.SCREEN_WIDTH  = this.resourceManager.STATE_VARIABLES.get("SCREEN_WIDTH").intValue();

        this.initGLFW();
        this.resourceManager.loadResources();

        this.timer              = new Timer();
        this.bullets            = new ArrayList<>();
        this.renderer           = new Renderer(this.window, resourceManager);
        this.background         = new Background();
        this.inputState         = new InputState(this.window);

        this.player             = this.resourceManager.getPlayer();

        final int waveIdx = this.resourceManager.STATE_VARIABLES.get("waveIdx").intValue();
        this.wave               = this.resourceManager.getWave(waveIdx);

        this.uiState = UIState.MAIN_MENU;
        this.uiMap = new HashMap<>();
        this.uiMap.put(UIState.MAIN_MENU, new MainMenuUI(this.window));
        this.uiMap.put(UIState.GAME_RUNNING, new GameRunningUI());
        this.uiMap.put(UIState.GAME_OVER_MENU, new GameOverUI());
        this.uiMap.put(UIState.PAUSE_MENU, new PauseMenuUI());
        this.uiMap.put(UIState.SETTINGS_MENU , new SettingsMenuUI());


    }

    public static void main(String[] args){
        Game game = new Game(); 
        game.runGame();
    }

}
