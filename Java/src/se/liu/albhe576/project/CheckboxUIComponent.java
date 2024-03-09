package se.liu.albhe576.project;

public class CheckboxUIComponent extends UIComponent{
    public boolean toggled;
    private final int checkmarkTextureId;
    private final float checkmarkWidth;
    private final float checkmarkHeight;
    public int getCheckmarkTextureId(){return this.checkmarkTextureId;}
    public float getCheckmarkWidth(){return this.checkmarkWidth;}
    public float getCheckmarkHeight(){return this.checkmarkHeight;}
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight, boolean initToggle){
        super(x,y,width,height,textureId);
        this.checkmarkTextureId = checkmarkTextureId;
        this.checkmarkWidth = checkmarkWidth;
        this.checkmarkHeight= checkmarkHeight;
        this.toggled = initToggle;
    }
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight){
        this(textureId, x, y, width, height, checkmarkTextureId, checkmarkWidth, checkmarkHeight, false);
    }
}
