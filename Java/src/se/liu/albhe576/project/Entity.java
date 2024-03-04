package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public abstract class Entity
{
    protected int hp;
    protected float x;
    protected float y;

    protected float width;
    protected float height;
    protected boolean alive;
    protected long lastUpdate;
    private final float rotation;
    private final int textureIdx;
    protected float xAcceleration;
    protected float yAcceleration;
    public int getTextureIdx(){
        return this.textureIdx;
    }
    public float getRotation(){
        return this.rotation;
    }
    protected Entity(int hp, float x, float y, float width, float height, int textureIdx, float rotation){
        this.hp             = hp;
        this.x              = x;
        this.y              = y;
        this.width          = width;
        this.height         = height;
        this.textureIdx     = textureIdx;
        this.xAcceleration  = 0.0f;
        this.yAcceleration  = 0.0f;
        this.lastUpdate     = 0;
        this.alive          = true;
        this.rotation       = rotation;
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
    public boolean isWithinBounds(){
        final int x = (int) (100.0f - this.width / 2);
        final int y = (int) (100.0f - this.height / 2);
        return !(this.x <= -x || this.x >= x || this.y <= -y || this.y >= y);
    }
}
