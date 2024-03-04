package se.liu.albhe576.project;

public class CheckboxUIComponent extends UIComponent{
    public boolean toggled;
    public int checkmarkTextureId;
    public float checkmarkWidth;
    public float checkmarkHeight;
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight, boolean initToggle){
        super(x,y,width,height,textureId);
        System.out.println(width + " " + height);

        this.checkmarkTextureId = checkmarkTextureId;
        this.checkmarkWidth = checkmarkWidth;
        this.checkmarkHeight= checkmarkHeight;
        this.toggled = initToggle;
    }
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight){
        this(textureId, x, y, width, height, checkmarkTextureId, checkmarkWidth, checkmarkHeight, false);
    }
}
