package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

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
    public final boolean editor;

    protected PlatformLayer(int width, int height, boolean editor){
        this.width = width;
        this.height = height;
        this.editor = editor;
        this.inputState = new InputState();
    }

    public abstract void drawEntities(ArrayList<Entity> entities);
    public abstract void drawLines(List<ScreenPoint> points);
}
