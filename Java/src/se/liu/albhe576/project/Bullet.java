package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    public final Entity parent;
    private final float yAcc;

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
		super(0, x, y, width, height, textureIdx, rotation);
		this.parent = shooter;
        this.lastUpdate = 0;
        this.yAcc = yAcc;
    }
    private long lastUpdate;

    public void update(long lastTick) {
		if(lastUpdate + 10 <= lastTick){
			lastUpdate = lastTick;
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


        float[] bulletBoundingBox = this.getBoundingBox();
        final float minBulletX = bulletBoundingBox[0];
        final float maxBulletX = bulletBoundingBox[1];
        final float minBulletY = bulletBoundingBox[2];
        final float maxBulletY = bulletBoundingBox[3];

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
            if(this.isWithinBounds() && this.collided(entity) && (this.parent.getClass() != entity.getClass())){
                this.alive = false;

                entity.hp -= 1;
                if(entity instanceof Player){
                    System.out.printf("Player has %d lives!\n", entity.hp);
                }

                if(entity.hp <= 0){
                    entity.alive = false;
                }
                return true;
            }
        }
        return false;
    }

}
