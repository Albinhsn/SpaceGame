package se.liu.albhe576.project;

import java.awt.*;

public class ButtonUI extends UIComponent{

    public float x;
    public float y;
    public float width;
    public float height;
    public String text;
    public float fontSize;
    public Color textColor;
    public int textureId;
    public boolean isPressed(InputState inputState){
        if(!inputState.isMouse1Released()){
            return false;

        }
       Point mousePos = inputState.getMousePosition();

       float mouseX = ((mousePos.x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * Game.SCREEN_WIDTH;
       float mouseY = ((mousePos.y / (float)Game.SCREEN_HEIGHT) * 2.0f - 1.0f) * -Game.SCREEN_HEIGHT;

       final float minX = this.x - this.width;
       final float maxX = this.x + this.width;
       final float minY = this.y - this.height;
       final float maxY = this.y + this.height;

       return minX <= mouseX && mouseX <= maxX && minY <= mouseY && mouseY <= maxY;
    }
    public ButtonUI(float x, float y, float width, float height, String text, int textureId, float fontSize, Color textColor){
        this.x          = x;
        this.y          = y;
        this.width      = width;
        this.height     = height;
        this.text       = text;
        this.textureId  = textureId;
        this.fontSize   = fontSize;
        this.textColor  = textColor;
    }
}
