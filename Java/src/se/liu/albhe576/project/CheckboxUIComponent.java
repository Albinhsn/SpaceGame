package se.liu.albhe576.project;

/**
 *
 */
public class CheckboxUIComponent extends UIComponent{
    /**
     *
     */
    public boolean toggled;
    /**
     *
     */
    public int checkmarkTextureId;
    /**
     *
     */
    public float checkmarkWidth;
    /**
     *
     */
    public float checkmarkHeight;

    /**
     * @param textureId
     * @param x
     * @param y
     * @param width
     * @param height
     * @param checkmarkTextureId
     * @param checkmarkWidth
     * @param checkmarkHeight
     * @param initToggle
     */
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight, boolean initToggle){
        super(x,y,width,height,textureId);

        this.checkmarkTextureId = checkmarkTextureId;
        this.checkmarkWidth = checkmarkWidth;
        this.checkmarkHeight= checkmarkHeight;
        this.toggled = initToggle;
    }

    /**
     * @param textureId
     * @param x
     * @param y
     * @param width
     * @param height
     * @param checkmarkTextureId
     * @param checkmarkWidth
     * @param checkmarkHeight
     */
    public CheckboxUIComponent(int textureId, float x, float y, float width, float height, int checkmarkTextureId, float checkmarkWidth, float checkmarkHeight){
        this(textureId, x, y, width, height, checkmarkTextureId, checkmarkWidth, checkmarkHeight, false);
    }
}
