package se.liu.albhe576.project;

import java.util.List;

public abstract class Entity
{
    protected int health;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected final float rotation;
    protected final int textureIdx;
    protected final float movementSpeed;
    public float getMovementSpeed(){
        return this.movementSpeed;
    }

    public int getTextureIdx(){
        return this.textureIdx;
    }

    public float getRotation(){
        return this.rotation;
    }

    protected Entity(int hp, float x, float y, float width, float height, int textureIdx, float rotation, float movementSpeed){
        this.health = hp;
        this.x              = x;
        this.y              = y;
        this.width          = width;
        this.height         = height;
        this.textureIdx     = textureIdx;
        this.rotation       = rotation;
        this.movementSpeed = movementSpeed;
    }

    protected boolean collided(Entity entity){
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

    protected boolean takeDamage(){
        this.health -= 1;
        return this.health <= 0;
    }
    private boolean checkBulletCollision(Entity target){
        if(!(this instanceof Bullet bullet)){
            return true;
        }
        return bullet.getParent().getClass() != target.getClass() && target.getClass() != Bullet.class;
    }

    public boolean checkCollision(Entity entity){
        return this.isWithinScreen() && this.collided(entity) && checkBulletCollision(entity);
    }

    public void handleCollision(Entity entity){
        if(this.checkCollision(entity)){
            this.takeDamage();
            entity.takeDamage();
        }
    }

    public void handleCollisions(List<Entity> entities){
        for(Entity entity : entities){
            this.handleCollision(entity);
        }
    }

    public boolean isAlive(){
        return this.health > 0;
    }

    public float[] getBoundingBox(){
        final float halvedEntityHeight = this.height;
        final float halvedEntityWidth = this.width;
        final float minEntityX = this.x - halvedEntityWidth;
        final float minEntityY = this.y - halvedEntityHeight;

        final float maxEntityX = this.x + halvedEntityWidth;
        final float maxEntityY = this.y + halvedEntityHeight;

        return new float[]{minEntityX, maxEntityX, minEntityY, maxEntityY};
    }

    public boolean isWithinScreen(){
        final int x = (int) (100.0f - this.width);
        final int y = (int) (100.0f - this.height);
        return !(this.x <= -x || this.x >= x || this.y <= -y || this.y >= y);
    }
}
