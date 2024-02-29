package se.liu.albhe576.project;

import java.util.Objects;

public class Enemy extends Entity
{
    private int type;
    private long lastUpdate;
    private boolean isAlive;
    private float spawnTime;

    public Enemy(final float x, final float y, final float width, final float height, int textureIdx, int type, float spawnTime)
    {
		super(x, y, width, height, textureIdx);
			this.type           = type;
			this.lastUpdate     = 0;
            this.isAlive        = false;
            this.spawnTime = spawnTime;
    }

    public boolean hasSpawned(long timeSinceWaveStarted){
        return timeSinceWaveStarted >= this.spawnTime;
    }

    @Override public void update(long startTime) {
        if(this.isAlive && this.hasSpawned){
            this.move(startTime);
        }
    }


    private void move(long tick){
		if(lastUpdate + 10 <= tick){
			final int moveSpeed = 3;
			this.y             -= moveSpeed;
			lastUpdate          = System.currentTimeMillis();

		}
    }
}
