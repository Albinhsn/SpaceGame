package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game
{
    private final ArrayList<Entity> entities;
    private final Renderer renderer;
    public static final int SCREEN_WIDTH = 620;
    public static final int SCREEN_HEIGHT = 480;


    private ArrayList<Entity> loadEntities() throws IOException {
        Bounds bounds = new Bounds(0f, 0f, 0, 0);
        Player player = new Player(0, 0);

        ArrayList<Entity> entities = new ArrayList<>();
        ArrayList<Entity> waveData = GameData.getLevel1();
        entities.add(player);
        entities.addAll(waveData);

        return entities;
    }

    public Game() throws IOException {
        this.entities = loadEntities();
        this.renderer = new Renderer(SCREEN_WIDTH, SCREEN_HEIGHT);
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
        Thread platformThread = this.renderer.runRenderer();

        while(platformThread.isAlive()){
            this.renderer.drawEntities(this.entities);
        }
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
