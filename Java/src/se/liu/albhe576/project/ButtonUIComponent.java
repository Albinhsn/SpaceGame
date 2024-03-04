package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

public class ButtonUIComponent extends UIComponent{
    public String text;
    public float fontSize;
    public Color textColor;
    public ButtonUIComponent(float x, float y, float width, float height, String text, int textureId, float fontSize, Color textColor){
        super(x,y,width,height,textureId);
        this.text           = text;
        this.fontSize       = fontSize;
        this.textColor      = textColor;
    }


}
