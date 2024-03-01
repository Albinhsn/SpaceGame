package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public abstract class Entity
{
    public float x;
    public float y;
    public float width;
    public float height;
    public boolean alive;

    private float rotation;
    private final int textureIdx;
    public int getTextureIdx(){
        return this.textureIdx;
    }
    public float getRotation(){
        return this.rotation;
    }
    protected float xAcceleration;
    protected float yAcceleration;
    protected Entity(float x, float y, float width, float height, int textureIdx, float rotation){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureIdx = textureIdx;
        this.xAcceleration = 0.0f;
        this.yAcceleration = 0.0f;
        this.alive = true;
        this.rotation = rotation;
    }

    public float[] getBoundingBox(){
        final float halvedEntityHeight = this.height / 2.0f;
        final float halvedEntityWidth = this.width / 2.0f;
        final float minEntityX = this.x - halvedEntityWidth;
        final float minEntityY = this.y - halvedEntityHeight;

        final float maxEntityX = this.x + halvedEntityWidth;
        final float maxEntityY = this.y + halvedEntityHeight;

        return new float[]{minEntityX, maxEntityX, minEntityY, maxEntityY};
    }
}
