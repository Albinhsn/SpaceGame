package se.liu.albhe576.project;

import java.util.List;

/**
 *
 */
public abstract class Entity
{
    /**
     *
     */
    protected int hp;
    /**
     *
     */
    protected float x;
    /**
     *
     */
    protected float y;
    /**
     *
     */
    protected int scoreGiven;

    /**
     *
     */
    protected float width;
    /**
     *
     */
    protected float height;
    /**
     *
     */
    protected boolean alive;
    /**
     *
     */
    private final float rotation;
    /**
     *
     */
    private final int textureIdx;
    /**
     *
     */
    protected float yAcceleration;

    /**
     * @return
     */
    public int getTextureIdx(){
        return this.textureIdx;
    }

    /**
     * @return
     */
    public float getRotation(){
        return this.rotation;
    }

    /**
     * @param hp
     * @param x
     * @param y
     * @param width
     * @param height
     * @param textureIdx
     * @param rotation
     * @param scoreGiven
     */
    protected Entity(int hp, float x, float y, float width, float height, int textureIdx, float rotation, int scoreGiven){
        this.hp             = hp;
        this.x              = x;
        this.y              = y;
        this.width          = width;
        this.height         = height;
        this.textureIdx     = textureIdx;
        this.alive          = true;
        this.rotation       = rotation;
        this.scoreGiven = scoreGiven;
    }

    /**
     * @param entity
     * @return
     */
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

    /**
     * @return
     */
    protected boolean takeDamage(){
        this.hp -= 1;
        if(this.hp <= 0){
            this.alive = false;
            return true;
        }
        return false;
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

    public boolean handleCollision(Entity entity){
        if(this.checkCollision(entity)){
            this.takeDamage();
            entity.takeDamage();
            return true;
        }
        return false;

    }

    public void handleCollisions(List<Entity> entities){
        for(Entity entity : entities){
            this.handleCollision(entity);
        }
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
        final int x = (int) (100.0f - this.width / 2);
        final int y = (int) (100.0f - this.height / 2);
        return !(this.x <= -x || this.x >= x || this.y <= -y || this.y >= y);
    }
}
