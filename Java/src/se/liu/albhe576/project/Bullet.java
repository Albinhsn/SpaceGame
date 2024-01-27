package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    private Entity shooter;

    private int yDirection;

    public Bullet(
	    final int x,
	    final int y,
	    final float textureWidth,
	    final float textureHeight,
	    final Texture texture,
	    final Bounds bounds,
	    Entity shooter,
	    int yDirection
    )
    {
	super(x, y, textureWidth, textureHeight, texture, bounds);
	this.yDirection = yDirection;
	this.shooter = shooter;
    }
    private long lastUpdate;

    @Override public void update(long startTime) {
	if(lastUpdate + 10 <= System.currentTimeMillis()){
	    lastUpdate = System.currentTimeMillis();
	    this.y += yDirection;
	}
    }

    public void checkCollision(List<Entity> entities){
	for(int i = 0; i < entities.size(); i++){
	    Entity entity = entities.get(i);
	    if(this.collided(entity) && !Objects.equals(this.shooter, entity)){
		entity.setAlive(false);
		this.alive = false;
		return;
	    }
	}
    }

    @Override public boolean isInScene() {
	if(this.y < -Game.SCREEN_HEIGHT || !this.alive){
	    return false;
	}
	return true;
    }
}
