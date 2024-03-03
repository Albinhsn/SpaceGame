package se.liu.albhe576.project;

import java.awt.*;

public class ButtonUI extends UIComponent{
    public String text;
    public float fontSize;
    public Color textColor;
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
