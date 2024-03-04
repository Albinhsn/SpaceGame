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
    private long endedAnimation;
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
        this.endedAnimation = 0;
    }
    public static final UnaryOperator<Float> easeOutCubic = (x) -> {
        return (float) (1 - Math.pow(1 - x, 3));
    };
    public static final UnaryOperator<Float> easeLinearly = (x) -> {
        return x;
    };
    public static final UnaryOperator<Float> easeInCubic = (x) -> {
        return x * x * x;
    };

    public void animate(InputState inputState, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction){
        this.animate(inputState, increasePerMs, maxSize, easeInFunction, easeInFunction);
    }
    public void animate(InputState inputState, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        long tick = System.currentTimeMillis();
        if(this.hovers(inputState)){
            if(this.startedAnimation == 0){
                this.startedAnimation = tick;
            }
            long tickDifference = tick - this.startedAnimation;
            float increase = easeInFunction.apply(Math.min(increasePerMs * tickDifference, 1));

            this.width  = this.initialWidth + maxSize * increase;
            this.height = this.initialHeight + maxSize * increase;
            this.endedAnimation = tick;
        }else{
            if(this.endedAnimation != 0){
                long tickDifference = tick - this.endedAnimation;
                float increase = easeOutFunction.apply(1.0f - Math.min(increasePerMs * tickDifference, 1));

                if(increase <= 0){
                    this.endedAnimation = 0;
                }

                this.width  = this.initialWidth + maxSize * increase;
                this.height = this.initialHeight + maxSize * increase;
            }else{
                this.width = initialWidth;
                this.height = initialHeight;
            }
            this.startedAnimation = 0;
        }

    }

}
