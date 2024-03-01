package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wave {
    private long startTime;
    private List<Enemy> enemies;
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
    public List<Bullet> updateWave(long startTime){
        // Check if they have spawned or not
        List<Bullet> bullets = new ArrayList<>();
        for(Enemy enemy : enemies.stream().filter(e -> e.hasSpawned(startTime)).toList()){
           Bullet bullet = enemy.update();
           if(bullet != null){
               bullets.add(bullet);
           }
        }
        return bullets;
    }
}
