package se.liu.albhe576.project;

import java.awt.*;
import java.util.function.UnaryOperator;

public class ButtonUI extends UIComponent{
    static class Animation{
        public Animation(){

        }
    }
    public String text;
    public float fontSize;
    public Color textColor;
    private final float initialWidth;
    private final float initialHeight;
    private long startedAnimation;
    public ButtonUI(float x, float y, float width, float height, String text, int textureId, float fontSize, Color textColor){
        this.x              = x;
        this.y              = y;
        this.width          = width;
        this.initialWidth   = width;
        this.height         = height;
        this.initialHeight  = height;
        this.text           = text;
        this.textureId      = textureId;
        this.fontSize       = fontSize;
        this.textColor      = textColor;
        this.startedAnimation = 0;
    }
    private final UnaryOperator<Float> easeOutCubic = (x) -> {
        return (float) (1 - Math.pow(1 - x, 3));
    };
    private final UnaryOperator<Float> easeLinearly = (x) -> {
        return x;
    };
    private final UnaryOperator<Float> easeInCubic = (x) -> {
        return x * x * x;
    };

    private void animate(InputState inputState, float increasePerMs, float maxSize, UnaryOperator<Float> easeFunction){
        long tick = System.currentTimeMillis();
        if(this.hovers(inputState)){
            if(startedAnimation == 0){
                this.startedAnimation = tick;
            }
            long tickDifference = tick - this.startedAnimation;
            float increase = easeFunction.apply(Math.min(increasePerMs * tickDifference, 1));

            this.width  = this.initialWidth + maxSize * increase;
            this.height = this.initialHeight + maxSize * increase;
        }else{
            this.startedAnimation = 0;
            this.width = initialWidth;
            this.height = initialHeight;
        }

    }

    public void animateEaseInCubic(InputState inputState, float increasePerMs, float maxSize){
        animate(inputState, increasePerMs, maxSize, this.easeInCubic);
    }

    public void animateEaseOutCubic(InputState inputState, float increasePerMs, float maxSize){
        animate(inputState, increasePerMs, maxSize, this.easeOutCubic);
    }
    public void animateLinearly(InputState inputState, float increasePerMs, float maxSize){
        animate(inputState, increasePerMs, maxSize, this.easeLinearly);
    }
}
