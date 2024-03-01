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
        final float halvedEntityHeight = entity.height / 2.0f;
        final float halvedEntityWidth = entity.width / 2.0f;
        final float minEntityX = entity.x - halvedEntityWidth;
        final float minEntityY = entity.y - halvedEntityHeight;

        final float maxEntityX = entity.x + halvedEntityWidth;
        final float maxEntityY = entity.y + halvedEntityHeight;

        final float halvedBulletHeight = this.height / 2.0f;
        final float halvedBulletWidth = this.width / 2.0f;
        final float minBulletX = this.x - halvedBulletWidth;
        final float minBulletY = this.y - halvedBulletHeight;

        final float maxBulletX = this.x + halvedBulletWidth;
        final float maxBulletY = this.y + halvedBulletHeight;

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
