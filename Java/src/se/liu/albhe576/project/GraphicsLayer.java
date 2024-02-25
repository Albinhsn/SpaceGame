package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;

public abstract class GraphicsLayer implements Runnable
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

    protected GraphicsLayer(int width, int height){
        this.width = width;
        this.height = height;
        this.inputState = new InputState();
    }

    public abstract void drawEntities(List<Entity> entities);

}
