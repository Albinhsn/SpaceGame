package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wave {
    private long startTime;
    private List<Enemy> enemies;
    private List<Path> paths;
    public Wave(List<Enemy> enemies){
        this.enemies = enemies;
    }
    public void removeKilledEnemies(){
        this.enemies.removeIf(enemy -> !enemy.alive);
    }
    public List<Entity> getEnemies(){
        return this.enemies.stream().map(enemy -> (Entity)enemy).collect(Collectors.toList());
    }
    public void setStartTime(long startTime){
        this.startTime = startTime;
    }
    public List<Bullet> updateWave(long lastTick, ResourceManager resourceManager){
        List<Bullet> bullets = new ArrayList<>();
        for(Enemy enemy : enemies.stream().filter(e -> e.hasSpawned(lastTick)).toList()){
           if(enemy.update(lastTick)){
               Bullet bullet = resourceManager.createNewBullet(enemy);
               bullets.add(bullet);
           }
        }
        return bullets;
    }
}
