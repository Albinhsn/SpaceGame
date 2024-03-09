package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private final List<Enemy> enemies;
    private final long timeWaveStarted;
    public boolean isOver(){
        return this.enemies.isEmpty();
    }

    private boolean isOutOfBounds(Enemy enemy){
        float[] bb = enemy.getBoundingBox();
        final float minEntityX = bb[0];
        final float minEntityY = bb[2];

        final float maxEntityX = bb[1];
        final float maxEntityY = bb[3];
        // This should be given from the wave data or something
        return (minEntityX < -160.0f || maxEntityX > 160.0f|| minEntityY < -160.0f || maxEntityY > 160.0f);
    }
    private void removeKilledEnemies() {
        this.enemies.removeIf(enemy -> !enemy.alive);
    }
    private void removeOutOfBoundsEnemies() {
        this.enemies.removeIf(this::isOutOfBounds);
    }
    public void removeDeadOrOutOfBoundsEnemies(){
        this.removeKilledEnemies();
        this.removeOutOfBoundsEnemies();
    }
    public List<Entity> getEnemiesAsEntities() {
        return this.enemies.stream().map(x -> (Entity)x).toList();
    }
    public List<Bullet> updateWave(long lastTick, EntityManager entityManager) {
        List<Bullet> bullets = new ArrayList<>();
        for (Enemy enemy : this.enemies){
            if (enemy.update(this.timeWaveStarted, lastTick)) {
                bullets.addAll(entityManager.getBullets(enemy));
            }
        }
        return bullets;
    }
    public Wave(List<Enemy> enemies, long timeWaveStarted){
        this.enemies = enemies;
        this.timeWaveStarted = timeWaveStarted;
    }

}
