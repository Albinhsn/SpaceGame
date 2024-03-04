package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

public class ButtonUIComponent extends UIComponent{
    public String text;
    public float fontSize;
    public Color textColor;
    public ButtonUIComponent(float x, float y, float width, float height, String text, float fontSize){
        super(x,y,width,height, ResourceManager.STATE_VARIABLES.get("buttonTextureIdMapKey").intValue());
        this.text           = text;
        this.fontSize       = fontSize;
        this.textColor      = Color.RED;
    }


}
