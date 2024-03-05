package se.liu.albhe576.project;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity
{
    public final int pathId;
    public final long spawnTime;
    private long lastShot;
    public final float moveSpeed;
    public final int type;
    private final Random rng = new Random();

    public boolean isOutOfBounds(){
        float[] bb = this.getBoundingBox();
        final float minEntityX = bb[0];
        final float minEntityY = bb[2];

        final float maxEntityX = bb[1];
        final float maxEntityY = bb[3];
        // This should be given from the wave data or something
        return (minEntityX < -160.0f || maxEntityX > 160.0f|| minEntityY < -160.0f || maxEntityY > 160.0f);
    }

    public Enemy(final int hp, final int enemyType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int pathId, int scoreGiven, float movementSpeed)
    {
		super(hp, x, y, width, height, textureIdx, 0.0f, scoreGiven);
        this.type           = enemyType;
        this.pathId         = pathId;
        this.alive          = true;
        this.spawnTime      = spawnTime;
        this.moveSpeed      = movementSpeed;
        this.lastShot       = 0;
    }

    public boolean hasSpawned(long lastTick){
        return lastTick >= this.spawnTime;
    }


    public boolean willShoot(long lastTick){
        final long lowerBound = ResourceManager.STATE_VARIABLES.get("enemyGCDMin").longValue();
        final long upperBound = ResourceManager.STATE_VARIABLES.get("enemyGCDMax").longValue();

        final long gcd = rng.nextLong(lowerBound, upperBound);
        if(this.lastShot <= lastTick - gcd){
            this.lastShot = lastTick + gcd;
            return true;
        }
        return false;

    }

    public boolean update(long lastTick) {
        if(this.alive){
            this.move(lastTick);
            return this.willShoot(lastTick);
        }
        return false;
    }


    private void move(long lastTick){
        switch(this.pathId){
            case 0:{
                this.x += (float) Math.sin((double) lastTick / 500) / 5.0f;
                this.y             += (float) Math.cos((double) lastTick / 500) / 5.0f + this.moveSpeed;
                break;
            }
            case 1:{
                this.x += (float) Math.sin((double) lastTick / 1000) / 5.0f;
                this.y += this.moveSpeed;
                break;
            } case 2:{
                this.y             += (float) (Math.cos((double) lastTick / 500)) / 5.0f + this.moveSpeed * 0.5f;
               break;
            }
            case 3:{
                if(this.y >= 50.0f){
                   this.y -= 0.2f;
                }
                this.x             += (float) Math.cos((double) lastTick / 500) / 5.0f;
                break;
            }
            default:{
                System.out.printf("How did this happen to me %d\n", this.pathId);
                System.exit(1);
            }
        }
    }
}
