package se.liu.albhe576.project;

public class CheckboxUI extends UIComponent{
    public boolean toggled;
    public int checkmarkTextureId;
    public float checkmarkWidth;
    public float checkmarkHeight;
    public CheckboxUI( int textureId, float x, float y,  float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight, boolean initToggle){
        this.textureId = textureId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.checkmarkTextureId = checkmarkTextureId;
        this.checkmarkWidth = checkmarkWidth;
        this.checkmarkHeight= checkmarkHeight;
        this.toggled = initToggle;
    }
    public CheckboxUI(int textureId, float x, float y,  float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight){
        this(textureId, x, y, width, height, checkmarkTextureId, checkmarkWidth, checkmarkHeight, false);
    }
}
