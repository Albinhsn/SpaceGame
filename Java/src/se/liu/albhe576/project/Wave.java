package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wave {
    private List<Enemy> enemies;
    public Wave(List<Enemy> enemies){
        this.enemies = enemies;
    }
    public void removeKilledEnemies(){
        this.enemies.removeIf(enemy -> !enemy.alive);
    }
    public void removeOutOfBoundsEnemies(){
        this.enemies.removeIf(enemy -> enemy.isOutOfBounds());
    }
    public List<Enemy> getEnemies(){
        return this.enemies;
    }
    public List<Entity> getEnemiesAsEntities(){
        return this.enemies.stream().map(enemy -> (Entity)enemy).collect(Collectors.toList());
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
