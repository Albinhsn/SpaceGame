package se.liu.albhe576.project;

import java.awt.*;

public class SliderUIComponent extends UIComponent{

    public float value;
    private final float minValue;
    private final float maxValue;

    public void updateValue(){
        float minSliderX = this.x - this.width + 10 + this.sliderWidth;
        float maxSliderX = this.x + this.width - 10 - this.sliderWidth;

        this.value = this.minValue + (this.maxValue - this.minValue) * (this.sliderX - minSliderX) / (maxSliderX - minSliderX);
    }
    public void updateSliderPosition(int x){
        this.sliderX = ((x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * 100.0f;
        // ToDo This should be width / 2?
        float offset = this.width - 10 - this.sliderWidth;
        if(this.sliderX <= this.x - offset){
            this.sliderX = this.x - offset;
        }else if(this.sliderX >= this.x + offset){
            this.sliderX = this.x + offset;
        }
        this.updateValue();
    }
    public int sliderTextureId;
    public float sliderX;
    public float sliderY;
    public float sliderWidth;
    public float sliderHeight;
    public SliderUIComponent(int textureId, float x, float y, float width, float height, int sliderTextureId, float sliderWidth, float sliderHeight, float minValue, float maxValue){
        super(x,y,width,height,textureId);

        this.sliderTextureId    = sliderTextureId;
        this.sliderX            = x;
        this.sliderY            = y;
        this.sliderWidth        = sliderWidth;
        this.sliderHeight       = sliderHeight;
        this.value              = maxValue - (maxValue - minValue) / 2;
        this.minValue           = minValue;
        this.maxValue           = maxValue;
    }
}
