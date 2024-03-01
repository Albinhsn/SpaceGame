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
        this.enemies = this.enemies.stream().filter(enemy -> enemy.alive).collect(Collectors.toList());
    }
    public List<Entity> getEnemies(){
        return this.enemies.stream().map(enemy -> (Entity)enemy).collect(Collectors.toList());
    }
    public void setStartTime(long startTime){
        this.startTime = startTime;
    }
    public void updateWave(long startTime){
        for(Enemy enemy : enemies){
           enemy.update(startTime);
        }
    }
}
