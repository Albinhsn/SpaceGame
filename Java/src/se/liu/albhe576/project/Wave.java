package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

public class Wave {

    private long startTime;
    private final List<Enemy> enemies;
    public Wave(){
        this.enemies = new ArrayList<>();
    }
    public void updateWave(long startTime){
        for(Enemy enemy : enemies){
           enemy.update(startTime);
        }
    }
}
