package se.liu.albhe576.project;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Enemy extends Entity
{
    private int pathId;
    private long lastUpdate;
    private long spawnTime;
    private final float moveSpeed;
    private long lastShot;

    public Enemy(final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int pathId)
    {
		super(x, y, width, height, textureIdx, 0.0f);
			this.pathId         = pathId;
			this.lastUpdate     = 0;
            this.alive          = true;
            this.spawnTime      = spawnTime;
            this.moveSpeed      = x > 0 ? -3 : 3;
            this.lastShot       = 0;
    }

    public boolean hasSpawned(long timeSinceWaveStarted){
        return System.currentTimeMillis() - timeSinceWaveStarted >= this.spawnTime;
    }

    final Random rng = new Random();

    public boolean willShoot(){
        final long gcd = rng.nextLong(400, 1000);
        long timer = System.currentTimeMillis();
        if(this.lastShot <= timer - gcd){
            this.lastShot = timer + gcd;
            return true;
        }
        return false;

    }

    public Bullet update() {
        if(this.alive){
            this.move(System.currentTimeMillis());
            if(this.willShoot()){{
                return ResourceManager.createNewBullet(this, -1);
            }}
        }
        return null;
    }


    private void move(long tick){
		if(lastUpdate + 10 <= tick){
			this.x             += moveSpeed;
			lastUpdate          = tick;
		}
    }
}
