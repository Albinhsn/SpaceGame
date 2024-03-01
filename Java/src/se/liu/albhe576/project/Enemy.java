package se.liu.albhe576.project;

import java.util.Objects;

public class Enemy extends Entity
{
    private int pathId;
    private long lastUpdate;
    private boolean isAlive;
    private long spawnTime;

    public Enemy(final float x, final float y, final float width, final float height, int textureIdx, float spawnTime, int pathId)
    {
		super(x, y, width, height, textureIdx);
			this.pathId         = pathId;
			this.lastUpdate     = 0;
            this.isAlive        = true;
            this.spawnTime      = (long)(spawnTime * 1000);
    }

    public boolean hasSpawned(long timeSinceWaveStarted){
        return System.currentTimeMillis() - timeSinceWaveStarted >= this.spawnTime;
    }

    @Override public void update(long startTime) {
        if(this.isAlive && this.hasSpawned(startTime)){
            this.move(System.currentTimeMillis());
        }
    }


    private void move(long tick){
		if(lastUpdate + 10 <= tick){
			final int moveSpeed = 3;
			this.y             -= moveSpeed;
			lastUpdate          = tick;
            System.out.printf("Moved to %f %f\n", this.x, this.y);

		}
    }
}
