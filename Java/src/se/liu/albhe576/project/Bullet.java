package se.liu.albhe576.project;

import java.util.List;

public class Bullet extends Entity
{
    private final Entity    parent;
    private final float     yAcc;
    public Entity getParent(){
        return this.parent;
    }
    public void update() {
        this.y += this.yAcc;
    }

    private boolean collided(Entity entity){
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
        return !(minEntityY > maxBulletY) && !(maxEntityY < minBulletY);
    }

    public boolean checkCollision(Entity entity){
        if(!this.isWithinBounds()){
            return false;
        }

        if(entity.isWithinBounds() && this.collided(entity) && (this.parent.getClass() != entity.getClass())){
            this.alive = false;

            entity.hp -= 1;
            if(entity.hp <= 0){
                entity.alive = false;
                return true;
            }
            return false;
        }
        return false;
    }
    public boolean checkCollision(List<Entity> entities){
        if(!this.isWithinBounds()){
            return false;
        }

        for(Entity entity : entities){
            if(this.checkCollision(entity)){
                return true;
            }
        }
        return false;
    }
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
        this.yAcc = yAcc;
    }
}
