package se.liu.albhe576.project;

import java.awt.*;

public class SliderUI extends UIComponent{

    public float value;
    private final float minValue;
    private final float maxValue;

    public boolean isPressed(InputState inputState){
        if(!inputState.isMouse1Pressed()){
            return false;
        }
        Point mousePos = inputState.getMousePosition();

        float mouseX = ((mousePos.x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * Game.SCREEN_WIDTH;
        float mouseY = ((mousePos.y / (float)Game.SCREEN_HEIGHT) * 2.0f - 1.0f) * -Game.SCREEN_HEIGHT;

        final float minX = this.sliderX - this.sliderWidth;
        final float maxX = this.sliderX + this.sliderWidth;
        final float minY = this.sliderY - this.sliderHeight;
        final float maxY = this.sliderY + this.sliderHeight;

        return minX <= mouseX && mouseX <= maxX && minY <= mouseY && mouseY <= maxY;
    }
    public void updateValue(){
        float minSliderX = this.x - this.width + 10 + this.sliderWidth;
        float maxSliderX = this.x + this.width - 10 - this.sliderWidth;

        this.value = this.minValue + (this.maxValue - this.minValue) * (this.sliderX - minSliderX) / (maxSliderX - minSliderX);
    }
    public void updateSliderPosition(int x){
        this.sliderX =((x / (float)Game.SCREEN_WIDTH) * 2.0f - 1.0f) * Game.SCREEN_WIDTH ;
        // ToDo This should be width / 2?
        if(this.sliderX <= this.x - this.width + 10 + this.sliderWidth){
            this.sliderX = this.x - this.width + 10 + this.sliderWidth;
        }else if(this.sliderX >= this.x + this.width - 10 - this.sliderWidth){
            this.sliderX = this.x + this.width - 10 - this.sliderWidth;
        }
        this.updateValue();
    }
    public int sliderTextureId;
    public float sliderX;
    public float sliderY;
    public float sliderWidth;
    public float sliderHeight;
    public SliderUI(int textureId, float x, float y, float width, float height, int sliderTextureId, float sliderX, float sliderY, float sliderWidth, float sliderHeight, float minValue, float maxValue){
        this.textureId          = textureId;
        this.x                  = x;
        this.y                  = y;
        this.width              = width;
        this.height             = height;
        this.sliderTextureId    = sliderTextureId;
        this.sliderX            = sliderX;
        this.sliderY            = sliderY;
        this.sliderWidth        = sliderWidth;
        this.sliderHeight       = sliderHeight;
        this.value = maxValue - (maxValue - minValue) / 2;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
