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
    public final boolean editor;

    protected GraphicsLayer(int width, int height, boolean editor){
        this.width = width;
        this.height = height;
        this.editor = editor;
        this.inputState = new InputState();
    }

    public abstract void drawText(String text, int fontSize, int x, int y, Color color);

    public abstract void drawEntities(List<Entity> entities);

    // Used in current editor context
    public abstract void drawLines(List<ScreenPoint> points);
}
