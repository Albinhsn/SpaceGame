package se.liu.albhe576.project;

import java.awt.*;
import java.util.Objects;

public abstract class Entity
{
    public float x;
    public float y;
    public float width;
    public float height;

    private final int textureId;

    public int getTextureId(){
        return this.textureId;
    }

    private float xAcceleration;

    private float yAcceleration;

    public abstract void update(long startTime);

    protected Entity(float x, float y, float width, float height, int textureId){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureId = textureId;
        this.xAcceleration = 0.0f;
        this.yAcceleration = 0.0f;
    }
}
