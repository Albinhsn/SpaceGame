package se.liu.albhe576.project;

public abstract class Entity
{
    public float x;
    public float y;
    public float z;
    private Bounds bounds;
    private float textureWidth;
    private float textureHeight;
    public float[] getTextureSize(){
        return new float[]{this.textureWidth, textureHeight};
    };
    public Bounds getBounds(){
        return this.bounds;
    }

    private Texture texture;
    private byte bufferId;
    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }
    public float getZ(){
        return this.y;
    }
    public float getTextureWidth(){
        return this.textureWidth;
    }
    public float getTextureHeight(){
        return this.textureHeight;
    }

    public Texture getTexture(){
        return this.texture;
    }
    public abstract void update();

    public abstract boolean isInScene();

    public Entity(float x, float y, float z, float width, float height, Texture texture, Bounds bounds){
        this.x = x;
        this.y = y;
        this.z = z;
        this.textureWidth = width;
        this.textureHeight = height;
        this.texture = texture;
        this.bounds = bounds;
    }
}
