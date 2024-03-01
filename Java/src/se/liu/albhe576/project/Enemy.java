package se.liu.albhe576.project;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity
{
    private int pathId;
    private long lastUpdate;
    private long spawnTime;
    private long lastShot;
    private final float moveSpeed;
    public final int type;

    public Enemy(final int hp, final int enemyType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int pathId)
    {
		super(hp, x, y, width, height, textureIdx, 0.0f);
            this.type = enemyType;
			this.pathId         = pathId;
			this.lastUpdate     = 0;
            this.alive          = true;
            this.spawnTime      = spawnTime;
            this.moveSpeed      = x > 0 ? -1 : 1;
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
                    this.y += (float) Math.sin((double) lastUpdate / 500) * 2;
                    this.x             += (float) (Math.cos((double) lastUpdate / 500) * 2) + this.moveSpeed * 2.0f;
                    break;
                }
                case 1:{
                    this.y += (float) Math.sin((double) lastUpdate / 1000) * 2;
                    this.x += this.moveSpeed;
                    break;
                } case 2:{
                    this.x             += (float) (Math.cos((double) lastUpdate / 500) * 2) + this.moveSpeed * 0.5f;
                   break;
                }
                case 4:{
                    if(this.y >= Game.SCREEN_HEIGHT * 0.5f){
                       this.y -= 2.0f;
                    }
                    this.x             += (float) (Math.cos((double) lastUpdate / 500) * 2);
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
