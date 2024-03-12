package se.liu.albhe576.project;

import java.util.Random;

public class Enemy extends Entity{
    private final long spawnTime;
    private long lastShot;
    private final IAccelerationFunction yIAccelerationFunction;
    private final IAccelerationFunction xIAccelerationFunction;
    private final int scoreGiven;
    public final int type;
    public int getScoreGiven(){
        return this.scoreGiven;
    }
    public boolean hasSpawned(long timeWaveStarted, long lastTick){
        return lastTick >= this.spawnTime + timeWaveStarted;
    }
    private boolean willShoot(long lastTick){
        final Random rng        = new Random();
        final long lowerBound   = ResourceManager.STATE_VARIABLES.getOrDefault("enemyGCDMin", 400.0f).longValue();
        final long upperBound   = ResourceManager.STATE_VARIABLES.getOrDefault("enemyGCDMax", 1000.0f).longValue();

        final long gcd = rng.nextLong(lowerBound, upperBound);
        if(this.lastShot <= lastTick){
            this.lastShot = lastTick + gcd;
            return true;
        }
        return false;

    }

    private void move(long lastTick){
        float xOffset = this.xIAccelerationFunction.apply(lastTick, this);
        float yOffset = this.yIAccelerationFunction.apply(lastTick, this);
        this.x += xOffset;
        this.y -= yOffset;
    }
    public boolean update(long timeWaveStarted, long lastTick) {
        if(this.hasSpawned(timeWaveStarted, lastTick) && this.isAlive()){
            move(lastTick);
            return willShoot(lastTick);
        }
        return false;
    }
    public Enemy(final int hp, final int enemyType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int scoreGiven, float movementSpeed, IAccelerationFunction xIAccelerationFunction, IAccelerationFunction yIAccelerationFunction)
    {
        super(hp, x, y, width, height, textureIdx, 0.0f, movementSpeed);
        this.type           = enemyType;
        this.spawnTime      = spawnTime;
        this.lastShot       = 0;
        this.scoreGiven = scoreGiven;


        this.xIAccelerationFunction = xIAccelerationFunction;
        this.yIAccelerationFunction = yIAccelerationFunction;

    }
}
