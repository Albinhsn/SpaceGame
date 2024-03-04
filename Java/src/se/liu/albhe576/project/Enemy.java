package se.liu.albhe576.project;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity
{
    public final int pathId;
    private long lastUpdate;
    public final long spawnTime;
    private long lastShot;
    private final float moveSpeed;
    public final int type;

    public boolean isOutOfBounds(){
        float[] bb = this.getBoundingBox();
        final float minEntityX = bb[0];
        final float minEntityY = bb[2];

        final float maxEntityX = bb[1];
        final float maxEntityY = bb[3];
        // This should be given from the wave data or something
        return (minEntityX < -160.0f || maxEntityX > 160.0f|| minEntityY < -160.0f || maxEntityY > 160.0f);
    }

    public Enemy(final int hp, final int enemyType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int pathId)
    {
		super(hp, x, y, width, height, textureIdx, 0.0f);
            this.type = enemyType;
			this.pathId         = pathId;
			this.lastUpdate     = 0;
            this.alive          = true;
            this.spawnTime      = spawnTime;
            this.moveSpeed      = x > 0 ? -0.2f : 0.2f;
            this.lastShot       = 0;
    }

    public boolean hasSpawned(long lastTick){
        return lastTick >= this.spawnTime;
    }

    final Random rng = new Random();

    public boolean willShoot(long lastTick){
        final long gcd = rng.nextLong(400, 1000);
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


    private void move(long tick){
		if(lastUpdate + 10 <= tick){
            switch(this.pathId){
                case 0:{
                    this.y += (float) Math.sin((double) lastUpdate / 500) / 5.0f;
                    this.x             += (float) Math.cos((double) lastUpdate / 500) / 5.0f + this.moveSpeed;
                    break;
                }
                case 1:{
                    this.y += (float) Math.sin((double) lastUpdate / 1000) / 5.0f;
                    this.x += this.moveSpeed;
                    break;
                } case 2:{
                    this.x             += (float) (Math.cos((double) lastUpdate / 500)) / 5.0f + this.moveSpeed * 0.5f;
                   break;
                }
                case 3:{
                    if(this.y >= 50.0f){
                       this.y -= 0.2f;
                    }
                    this.x             += (float) Math.cos((double) lastUpdate / 500) / 5.0f;
                    break;
                }
                default:{
                    System.out.printf("How did this happen to me %d\n", this.pathId);
                    System.exit(1);
                }
            }
			lastUpdate          = tick;
		}
    }
}
