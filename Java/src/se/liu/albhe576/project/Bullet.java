package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    private Entity parent;

    private float yDirection;

    public Bullet(
	    float x,
	    float y,
	    float textureWidth,
	    float textureHeight,
	    final Texture texture,
	    final Bounds bounds,
	    Entity shooter,
	    float yDirection
    )
    {
		super(x, y, textureWidth, textureHeight, texture, bounds);
		this.yDirection = yDirection;
		this.parent = shooter;
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
			if(this.collided(entity) && !Objects.equals(this.parent, entity)){
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
