package se.liu.albhe576.project;

import java.awt.*;
import java.util.Objects;

public abstract class Entity
{
    public float x;
    public float y;
    public float width;
    public float height;

    private float rotation;

    private final int textureIdx;

    public int getTextureIdx(){
        return this.textureIdx;
    }
    public float getRotation(){
        return this.rotation;
    }

    private float xAcceleration;

    private float yAcceleration;

    public abstract void update(long startTime);

    protected Entity(float x, float y, float width, float height, int textureIdx){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureIdx = textureIdx;
        this.xAcceleration = 0.0f;
        this.yAcceleration = 0.0f;
    }
}
