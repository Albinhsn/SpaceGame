package se.liu.albhe576.project;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL40.*;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Game
{
    private long prevTick;
    private final Logger logger;
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
    private final Player player;
    private final Background background;
    private final Map<UIState, UI> uiMap;
    private final EntityManager entityManager;
    private UIState uiState;
    private int waveIdx;

    private void updateBullets(){
        this.bullets.removeIf(bullet -> !bullet.isWithinScreen());
        for (Bullet bullet : this.bullets) {
            bullet.update(timer.getLastTick());
        }
    }

    private void handleCollisions(){
        List<Entity> enemies = this.wave.getEnemiesAsEntities();
        for(Bullet bullet: this.bullets){
            bullet.handleCollisions(enemies);
            bullet.handleCollision(this.player);
        }
        this.bullets.removeIf(bullet -> !bullet.isAlive());

        for(Entity enemy : enemies){
            if(enemy.isAlive()){
                this.player.handleCollision(enemy);
            }
        }


    }
    public void updatePlayer(){
        if(player.updatePlayer(this.inputState, this.timer.getLastTick())){
            this.bullets.add(this.entityManager.createBullets(this.player));
        }
    }

    private boolean shouldHandleUpdates(){
        final long lastTick = this.timer.getLastTick();
        final int updateTimerMS = ResourceManager.STATE_VARIABLES.getOrDefault("updateTimerMS", 16.0f).intValue();

        if(lastTick >= this.lastUpdated + updateTimerMS){
            this.lastUpdated = lastTick;
            return true;
        }
        return false;
    }

    private void gatherScore(){
        for(Enemy enemy : this.wave.getEnemies()){
            if(!enemy.isAlive()){
                this.score += enemy.getScoreGiven();
            }
        }
    }

    private void updateGameEntities(){
        this.updatePlayer();
        this.wave.updateWave(this.bullets, this.timer.getLastTick(), this.entityManager);
        this.updateBullets();
    }

    private void gameLoop(){
        this.timer.updateTimer();

        if(this.shouldHandleUpdates()) {
            if(this.inputState.isKeyPressed(GLFW_KEY_P)){
                this.updateUIState(UIState.PAUSE_MENU);
            }
            this.updateGameEntities();
            this.handleCollisions();
            this.gatherScore();
            this.wave.removeDeadOrOutOfBoundsEnemies();
        }

        this.renderGameEntities();
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
        this.wave = this.entityManager.getWave(ResourceManager.STATE_VARIABLES.getOrDefault("waveIdx", 0.0f).intValue(), 0);
        this.bullets.clear();
        this.player.reset();
        this.timer.reset();
    }
    private void updateUIState(UIState newState){
        if(this.uiState == UIState.GAME_RUNNING && newState != UIState.GAME_RUNNING){
            this.timer.stopTimer();

        }
        if(this.uiState != UIState.SETTINGS_MENU && newState == UIState.SETTINGS_MENU){
            SettingsMenuUI settingsUI = (SettingsMenuUI)  this.uiMap.get(UIState.SETTINGS_MENU);
            settingsUI.parent = this.uiState;

        }
        if(this.uiState == UIState.GAME_OVER_MENU && newState != UIState.GAME_OVER_MENU){
            this.resetGame();
        }

        if(this.uiState != UIState.CONSOLE   && newState == UIState.CONSOLE){
            ConsoleUI consoleUI = (ConsoleUI)this.uiMap.get(UIState.CONSOLE);
            consoleUI.parent = this.uiState;
        }

        if(this.uiState == UIState.CONSOLE){
            // Check if any variable changed?
            int waveIdx = ResourceManager.STATE_VARIABLES.getOrDefault("waveIdx", 0.0f).intValue();
            if(waveIdx != this.waveIdx){
                this.logger.info(String.format("Changed waveIdx from %d -> %d\n", this.waveIdx, waveIdx));
                this.waveIdx = waveIdx;
                this.wave = this.entityManager.getWave(this.waveIdx, timer.getLastTick());
            }
        }

        if(newState == UIState.GAME_RUNNING && !this.timer.isRunning()){
            this.timer.startTimer();
        }

        this.uiState = newState;
    }
    private void getNextWave(){
        this.waveIdx++;
        ResourceManager.STATE_VARIABLES.put("waveIdx", (float)this.waveIdx);
        Wave nextWave = this.entityManager.getWave(this.waveIdx, this.timer.getLastTick());

        if(nextWave == null){
            this.uiState = UIState.GAME_OVER_MENU;
            ((GameOverUI) this.uiMap.get(UIState.GAME_OVER_MENU)).lostGame = false;
        }else{
            this.wave = nextWave;
        }
    }

    private void checkGameFinished(){
        if(!this.player.isAlive()){
            this.uiState = UIState.GAME_OVER_MENU;
            ((GameOverUI) this.uiMap.get(UIState.GAME_OVER_MENU)).lostGame = true;
        }else if(this.wave.isOver()){
            this.getNextWave();
        }
    }
    private void renderInfoStrings(){
        final float fontSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeSmall", 3.0f);
        final float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);
        final Color infoStringColor = Color.WHITE;
        final float x = 100.0f;
        float y = 60.0f;
        long ms = System.currentTimeMillis()  - this.prevTick;

        this.renderer.renderText(String.format("ms:%d", ms), x, y, spaceSize, fontSize, infoStringColor, TextLayoutEnum.ENDS_AT);
        String fpsString = String.format("fps:%d", Math.min((int)(1000.0f/ms), 999));
        y -= fontSize * 2;
        this.renderer.renderText(fpsString, x, y, spaceSize, fontSize, infoStringColor, TextLayoutEnum.ENDS_AT);
        this.prevTick = System.currentTimeMillis();
    }

    public void runGame(){

        while(!glfwWindowShouldClose(window)){
            this.initNewFrame();
            this.background.updateAndRender(this.renderer);
            this.renderInfoStrings();

            this.inputState.resetState();
            this.queryInputEvents();

            // This should be hoisted out after development
            if(ResourceManager.STATE_VARIABLES.containsKey("restartWave")){
                ResourceManager.STATE_VARIABLES.remove("restartWave");
                this.resetGame();
            }

            if(this.inputState.isKeyReleased(GLFW_KEY_C) && this.uiState != UIState.CONSOLE){
                this.inputState.resetState();
                updateUIState(UIState.CONSOLE);
            }

            if (this.uiState == UIState.GAME_RUNNING) {
                this.gameLoop();
                this.checkGameFinished();
            }

            UIState nextState = this.uiMap.get(this.uiState).render(this.inputState, this.renderer, this.window, this.score, this.player.health);
            updateUIState(nextState);
            glfwSwapBuffers(window);
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();

    }
    private void initGLFW(){

        final String title ="Jalaga";
        if(!glfwInit()){
            this.logger.severe("Failed to initialize GLFW");
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        this.window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, title, NULL, NULL);
        if(window == NULL){
            this.logger.severe("Failed to create the GLFW window");
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
        glfwSwapInterval(ResourceManager.STATE_VARIABLES.getOrDefault("vsync", 0.0f).intValue());

        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0,0,Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    }
    public Game() {

        this.logger             = Logger.getLogger("Game");
        this.score              = 0;
        this.prevTick           = 0;
        this.lastUpdated        = 0;
        ResourceManager resourceManager = new ResourceManager();
        Game.SCREEN_HEIGHT      = ResourceManager.STATE_VARIABLES.getOrDefault("SCREEN_HEIGHT", 480.0f).intValue();
        Game.SCREEN_WIDTH       = ResourceManager.STATE_VARIABLES.getOrDefault("SCREEN_WIDTH", 620.0f).intValue();
        this.waveIdx            = ResourceManager.STATE_VARIABLES.getOrDefault("waveIdx", 0.0f).intValue();


        this.initGLFW();
        resourceManager.loadResources();

        this.timer              = new Timer();
        this.bullets            = new ArrayList<>();
        this.renderer           = new Renderer(resourceManager);
        this.background         = new Background();
        this.inputState         = new InputState(this.window);
        this.entityManager      = new EntityManager();
        this.player             = this.entityManager.getPlayer();
        this.wave               = this.entityManager.getWave(this.waveIdx, 0);

        this.uiState            = UIState.MAIN_MENU;
        this.uiMap              = new HashMap<>();
        this.uiMap.put(UIState.CONSOLE, new ConsoleUI(this.window, resourceManager));
        this.uiMap.put(UIState.MAIN_MENU, new MainMenuUI(this.window));
        this.uiMap.put(UIState.GAME_RUNNING, new GameRunningUI());
        this.uiMap.put(UIState.GAME_OVER_MENU, new GameOverUI());
        this.uiMap.put(UIState.PAUSE_MENU, new PauseMenuUI());
        this.uiMap.put(UIState.SETTINGS_MENU , new SettingsMenuUI());
    }

    public static void main(String[] args){
        new Game().runGame();
    }

}
