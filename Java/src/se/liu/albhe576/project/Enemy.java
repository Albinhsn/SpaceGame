package se.liu.albhe576.project;

import java.util.Objects;

public class Enemy extends Entity
{
    private boolean spawned;
    private final float spawnTime;
    private int type;
    private boolean direction;

    private long lastUpdate;

    public Enemy(final int x, final int y, final float textureWidth, final float textureHeight, final Texture texture,
		 final Bounds bounds, float spawnTime, int type)
    {
	super(x, y, textureWidth, textureHeight, texture, bounds);
	this.spawnTime = spawnTime;
	this.alive = false;
	this.spawned = false;
	this.direction = x > 0.0f;
	this.type = type;
	this.lastUpdate = 0;
    }

    @Override public void update(long startTime) {
	long lastTick = System.currentTimeMillis();
	if(this.alive){
	    this.move(lastTick);
	}else if(!this.spawned && startTime + (long)this.spawnTime * 1000 <= lastTick){
	    this.alive = true;
	    this.spawned = true;
	}
    }

    private float sinYDiff(float x){
	return (float) Math.sin(8 * this.x) / 500.0f;
    }

    private void move(long tick){
	if(lastUpdate + 10 <= tick){
	    final int moveSpeed = 3;
	    this.y -= sinYDiff(this.x);
	    this.x -= this.direction ? moveSpeed : -moveSpeed;
	    lastUpdate = System.currentTimeMillis();

	}
    }

    @Override public boolean isInScene() {
	return  (!this.spawned && !this.alive) || this.spawned && this.alive;
    }
}
