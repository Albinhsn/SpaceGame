package se.liu.albhe576.project;

import java.util.Random;

public class Enemy extends Entity{
    private final long spawnTime;
    private long lastShot;
    private final AccelerationFunction yAccelerationFunction;
    private final AccelerationFunction xAccelerationFunction;
    public final int type;

    public boolean hasSpawned(long timeWaveStarted, long lastTick){
        return lastTick >= this.spawnTime + timeWaveStarted;
    }
    private boolean willShoot(long lastTick){
        final Random rng        = new Random();
        final long lowerBound   = ResourceManager.STATE_VARIABLES.get("enemyGCDMin").longValue();
        final long upperBound   = ResourceManager.STATE_VARIABLES.get("enemyGCDMax").longValue();

        final long gcd = rng.nextLong(lowerBound, upperBound);
        if(this.lastShot <= lastTick){
            this.lastShot = lastTick + gcd;
            return true;
        }
        return false;

    }

    private void move(long lastTick){
        float xOffset = this.xAccelerationFunction.apply(lastTick, this);
        float yOffset = this.yAccelerationFunction.apply(lastTick, this);
        this.x += xOffset;
        this.y -= yOffset;
    }
    public boolean update(long timeWaveStarted, long lastTick) {
        if(this.hasSpawned(timeWaveStarted, lastTick) && this.alive){
            move(lastTick);
            return willShoot(lastTick);
        }
        return false;
    }
    public Enemy copyEnemy(){
        return new Enemy(
                this.hp,
                this.type,
                this.x,
                this.y,
                this.width,
                this.height,
                this.textureIdx,
                this.spawnTime,
                this.scoreGiven,
                this.movementSpeed,
                this.xAccelerationFunction,
                this.yAccelerationFunction
        );

    }
    public Enemy(final int hp, final int enemyType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime,int scoreGiven, float movementSpeed, AccelerationFunction xAccelerationFunction, AccelerationFunction yAccelerationFunction)
    {
        super(hp, x, y, width, height, textureIdx, 0.0f, scoreGiven, movementSpeed);
        this.type           = enemyType;
        this.alive          = true;
        this.spawnTime      = spawnTime;
        this.lastShot       = 0;

        this.xAccelerationFunction = xAccelerationFunction;
        this.yAccelerationFunction = yAccelerationFunction;

    }
}
