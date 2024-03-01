package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    private final Entity parent;
    final float yAcc;

    public Bullet(
	    float x,
	    float y,
        float width,
        float height,
        int textureIdx,
	    Entity shooter,
        float yAcc,
        float rotation
    )
    {
		super(x, y, width, height, textureIdx, rotation);
		this.parent = shooter;
        this.lastUpdate = System.currentTimeMillis();
        this.yAcc = yAcc;
    }
    private long lastUpdate;

    public void update() {
		if(lastUpdate + 10 <= System.currentTimeMillis()){
			lastUpdate = System.currentTimeMillis();
			this.y += this.yAcc;
		}
    }

    private boolean collided(Entity entity){
        // ToDo hoist this out
        float[] entityBoundingBox = entity.getBoundingBox();
        final float minEntityX = entityBoundingBox[0];
        final float maxEntityX = entityBoundingBox[1];
        final float minEntityY = entityBoundingBox[2];
        final float maxEntityY = entityBoundingBox[3];


        final float minBulletX = entityBoundingBox[0];
        final float maxBulletX = entityBoundingBox[1];
        final float minBulletY = entityBoundingBox[2];
        final float maxBulletY = entityBoundingBox[3];

        if(minEntityX > maxBulletX || maxEntityX < minBulletX){
            return false;
        }
        if(minEntityY > maxBulletY || maxEntityY < minBulletY){
            return false;
        }
        return true;
    }

    public boolean checkCollision(List<Entity> entities){
        for(Entity entity : entities){
            if(this.collided(entity) && (this.parent.getClass() != entity.getClass())){
                this.alive = false;
                entity.alive = false;
                return true;
            }
        }
        return false;
    }

}
