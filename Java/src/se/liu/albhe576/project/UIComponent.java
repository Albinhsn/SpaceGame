package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

public class UIComponent {

    public void animate(InputState inputState, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        float[] newPosition = this.animation.animate(this.hovers(inputState), increasePerMs, maxSize, easeInFunction, easeOutFunction);
        this.width          = newPosition[0];
        this.height         = newPosition[1];
    }
    public void animate(InputState inputState, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction){
        this.animate(inputState, increasePerMs, maxSize, easeInFunction, easeInFunction); }
    public boolean hovers(InputState inputState){
        Point mousePos = inputState.getMousePosition();

        // get it between [-100,100]
        float mouseX = ((mousePos.x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * 100.0f;
        float mouseY = ((mousePos.y / (float)Game.SCREEN_HEIGHT) * 2.0f - 1.0f) * -100.0f;

        final float minX = this.x - this.width;
        final float maxX = this.x + this.width;
        final float minY = this.y - this.height;
        final float maxY = this.y + this.height;

        return minX <= mouseX && mouseX <= maxX && minY <= mouseY && mouseY <= maxY;
    }
    public boolean isPressed(InputState inputState){
        if(!inputState.isMouse1Pressed()){
            return false;

        }
        return this.hovers(inputState);
    }
    public boolean isReleased(InputState inputState){
        if(!inputState.isMouse1Released()){
            return false;

        }
        return this.hovers(inputState);
    }
    protected int   textureId;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected final Animation animation;

    public UIComponent(float x, float y,float width,float height, int textureId){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureId = textureId;
        this.animation = new Animation(width, height);

    }
}
