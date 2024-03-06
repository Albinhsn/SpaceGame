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

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param spaceSize
     * @param fontSize
     */
    public ButtonUIComponent(float x, float y, float width, float height, String text, float spaceSize, float fontSize){
        super(x,y,width,height, ResourceManager.STATE_VARIABLES.get("buttonTextureIdMapKey").intValue());
        this.text           = text;
        this.fontSize       = fontSize;
        this.spaceSize      = spaceSize;
        this.textColor      = Color.RED;
    }


}
