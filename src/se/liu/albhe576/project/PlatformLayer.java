package se.liu.albhe576.project;

import java.util.ArrayList;

public abstract class PlatformLayer implements Runnable
{

    public InputState inputState;
    private int height;
    private int width;

    public int getWidth(){
        return width;
    }
    public InputState getInputState(){
        return this.inputState;
    }
    public int getHeight(){
        return height;
    }

    protected PlatformLayer(int width, int height){
        this.width = width;
        this.height = height;
        this.inputState = new InputState();
    }

    public abstract void drawEntities(ArrayList<Entity> entities);
}
