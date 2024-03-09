package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

public class ButtonUIComponent extends UIComponent{
    private final String text;
    private final float fontSize;
    private final float spaceSize;
    private final Color textColor;
    public String getText(){return this.text;}
    public float getFontSize(){return this.fontSize;}
    public float getSpaceSize(){return this.spaceSize;}
    public Color getTextColor(){return this.textColor;}

    public ButtonUIComponent(float x, float y, float width, float height, String text, float spaceSize, float fontSize, Animation animation){
        super(x,y,width,height, Texture.GREY_BOX, animation);
        this.text           = text;
        this.fontSize       = fontSize;
        this.spaceSize      = spaceSize;
        this.textColor      = Color.RED;
    }
    public ButtonUIComponent(float x, float y, float width, float height, String text, float spaceSize, float fontSize){
        this(x,y,width,height, text,spaceSize, fontSize, null);
    }


}
