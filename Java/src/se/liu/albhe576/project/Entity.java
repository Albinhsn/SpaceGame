package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;
import java.util.Objects;

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

    /**
     * @param entity
     * @return
     */
    public boolean checkCollision(Entity entity){
        if(!this.isWithinBounds()){
            return false;
        }
        if(this.collided(entity)){
            this.takeDamage();
            return entity.takeDamage();
        }
        return false;
    }

    /**
     * @param entities
     * @return
     */
    public int checkCollision(List<Entity> entities){
        if(!this.isWithinBounds()){
            return -1;
        }

        for(Entity entity : entities){
            if(this.checkCollision(entity)){
                return entity.scoreGiven;
            }
        }
        return -1;
    }

    /**
     * @return
     */
    public float[] getBoundingBox(){
        final float halvedEntityHeight = this.height;
        final float halvedEntityWidth = this.width;
        final float minEntityX = this.x - halvedEntityWidth;
        final float minEntityY = this.y - halvedEntityHeight;

        final float maxEntityX = this.x + halvedEntityWidth;
        final float maxEntityY = this.y + halvedEntityHeight;

        return new float[]{minEntityX, maxEntityX, minEntityY, maxEntityY};
    }

    /**
     * @return
     */
    public boolean isWithinBounds(){
        final int x = (int) (100.0f - this.width / 2);
        final int y = (int) (100.0f - this.height / 2);
        return !(this.x <= -x || this.x >= x || this.y <= -y || this.y >= y);
    }
}
