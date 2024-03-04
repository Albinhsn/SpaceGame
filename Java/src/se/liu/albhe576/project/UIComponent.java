package se.liu.albhe576.project;

import java.awt.*;

public abstract class UIComponent {

    public boolean hovers(InputState inputState){
        Point mousePos = inputState.getMousePosition();

        float mouseX = ((mousePos.x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * Game.SCREEN_WIDTH;
        float mouseY = ((mousePos.y / (float)Game.SCREEN_HEIGHT) * 2.0f - 1.0f) * -Game.SCREEN_HEIGHT;

        final float minX = this.x - this.width;
        final float maxX = this.x + this.width;
        final float minY = this.y - this.height;
        final float maxY = this.y + this.height;

        return minX <= mouseX && mouseX <= maxX && minY <= mouseY && mouseY <= maxY;
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
}
