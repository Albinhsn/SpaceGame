package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

/**
 *
 */
public class ButtonUIComponent extends UIComponent{
    /**
     *
     */
    public String text;
    /**
     *
     */
    public float fontSize;
    /**
     *
     */
    public float spaceSize;
    /**
     *
     */
    public Color textColor;

    public ButtonUIComponent(float x, float y, float width, float height, String text, float spaceSize, float fontSize, Animation animation){
        super(x,y,width,height, ResourceManager.STATE_VARIABLES.get("buttonTextureIdMapKey").intValue(), animation);
        this.text           = text;
        this.fontSize       = fontSize;
        this.spaceSize      = spaceSize;
        this.textColor      = Color.RED;
    }
    public ButtonUIComponent(float x, float y, float width, float height, String text, float spaceSize, float fontSize){
        this(x,y,width,height, text,spaceSize, fontSize, null);
    }


}
