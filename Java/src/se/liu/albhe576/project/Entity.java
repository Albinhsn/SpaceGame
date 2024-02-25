package se.liu.albhe576.project;

import java.awt.*;
import java.util.Objects;

public abstract class Entity
{
    public float x;
    public float y;
    private Bounds bounds;
    private float textureWidth;
    private float textureHeight;
    public boolean alive;
    private Texture texture;

    public void setAlive(boolean alive){
        this.alive = alive;
    }
    public Bounds getBounds(){
        return this.bounds;
    }
    public float[] getTextureSize(){
        return new float[]{this.textureWidth, textureHeight};
    };

    public float getTextureWidth(){
        return this.textureWidth;
    }
    public float getTextureHeight(){
        return this.textureHeight;
    }

    public Texture getTexture(){
        return this.texture;
    }
    public abstract void update(long startTime);

    public float[] getBoundPosition(){
        Bounds bounds = this.getBounds();
        float boundX = this.x + bounds.getTextureOffsetX();
        float boundY = this.x + bounds.getTextureOffsetY();
        float leftX = boundX;
        float rightX = boundX + bounds.getWidth();
        float topY = boundY;
        float botY = boundY - bounds.getHeight();

        return new float[]{leftX, rightX, topY, botY};

    }
    public boolean collided(Entity entity){
        if(Objects.equals(entity, this)){
            return false;
        }
        float[]boundPosition = this.getBoundPosition();

        float left = boundPosition[0];
        float right = boundPosition[1];
        float top = boundPosition[2];
        float bottom = boundPosition[3];

        float[]targetBoundPosition = entity.getBoundPosition();

        float targetLeft = targetBoundPosition[0];
        float targetRight = targetBoundPosition[1];
        float targetTop = targetBoundPosition[2];
        float targetBottom = targetBoundPosition[3];

        boolean targetLeftWithinBounds = left <= targetLeft && targetLeft <= right;
        boolean targetRightWithinBounds = left <= targetRight && targetRight <= right;

        boolean targetTopWithinBounds = bottom <= targetTop && targetTop <= top;
        boolean targetBottomWithinBounds = bottom <= targetBottom && targetBottom <= top;

        return (targetLeftWithinBounds || targetRightWithinBounds) && (targetTopWithinBounds || targetBottomWithinBounds);
    }

    public abstract boolean isInScene();

    protected Entity(float x, float y, float textureWidth, float textureHeight, Texture texture, Bounds bounds){
        this.x = x;
        this.y = y;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = texture;
        this.bounds = bounds;
        this.alive = true;
    }
}
