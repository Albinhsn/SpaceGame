package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game
{
    private final ArrayList<Entity> entities;

    private final GraphicsLayer platformLayer;

    public static final int SCREEN_WIDTH = 620;
    public static final int SCREEN_HEIGHT = 480;


    private ArrayList<Entity> loadEntities() throws IOException {
        Texture texture = GameData.loadPNGFile("./resources/fonts/font01.png");
        Bounds bounds = new Bounds(0f, 0f, 0, 0, new Color(255, 255, 255, 255), 2, texture.getWidth(), texture.getHeight());
        Player entity = new Player(0, 0, 0f, 0f, texture, bounds);

        ArrayList<Entity> entities = new ArrayList<>();
        entities.add(entity);
        ArrayList<Entity> waveData = GameData.getLevel1();
        entities.addAll(waveData);

        return entities;
    }

    public Game() throws IOException {
        this.entities = loadEntities();
        this.platformLayer = new OpenGLGraphicsLayer(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private Thread runPlatformLayer(GraphicsLayer platformLayer){
        Thread platformThread = new Thread(platformLayer);
        platformThread.start();

        return platformThread;
    }

    private Player getPlayer(){
        return (Player) this.entities.get(0);
    }


    private void updateEntities(long startTime){
        for(int i = 0; i < entities.size(); i++){
            this.entities.get(i).update(startTime);
        }
        List<Entity> bullets =  this.entities.stream().filter(s -> s instanceof Bullet).toList();
        List<Entity> rest = this.entities.stream().filter(s -> !(s instanceof Bullet)).toList();
        for(Entity bulletEntity  : bullets){
            Bullet bullet = (Bullet) bulletEntity;
            bullet.checkCollision(rest);
        }
    }

    private long lastGC;
    private void handleGarbage(){
        final long gcCooldown = 1000;

        if(lastGC + gcCooldown <= System.currentTimeMillis()){
            lastGC = System.currentTimeMillis();
            this.entities.removeIf(s -> !s.isInScene());
        }
    }


    public void runGame(){
        Thread platformThread = this.runPlatformLayer(this.platformLayer);

        while(platformThread.isAlive()){
            //updateEntities(startTime);
            //handleInput();
            //this.entities.removeIf(s -> !s.isInScene());
            this.platformLayer.drawEntities(this.entities);
            // ToDo this should only be neccessary if we get multiple levels/waves
            // this.handleGarbage();
        }
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
