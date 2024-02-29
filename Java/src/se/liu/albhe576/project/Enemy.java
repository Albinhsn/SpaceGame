package se.liu.albhe576.project;

import java.util.Objects;

public class Enemy extends Entity
{
    private int type;
    private long lastUpdate;

    public Enemy(final float x, final float y, final float width, final float height, int textureIdx, int type)
    {
		super(x, y, width, height, textureIdx);
			this.type = type;
			this.lastUpdate = 0;
    }

    @Override public void update(long startTime) {
    }


    private void move(long tick){
		if(lastUpdate + 10 <= tick){
			final int moveSpeed = 3;
			this.y -= moveSpeed;
			lastUpdate = System.currentTimeMillis();

		}
    }
}
