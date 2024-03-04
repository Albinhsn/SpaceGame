package se.liu.albhe576.project;

import java.util.function.UnaryOperator;

public class Animation {

    private final float initialWidth;
    private final float initialHeight;
    private long        startedAnimation;
    private long        endedAnimation;

    public static final UnaryOperator<Float> easeOutCubic = (x) -> (float) (1 - Math.pow(1 - x, 3));
    public static final UnaryOperator<Float> easeLinearly = (x) -> x;
    public static final UnaryOperator<Float> easeInCubic = (x) -> x * x * x;

    public float[] animate(boolean hovers, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        long tick = System.currentTimeMillis();
        float width = initialWidth;
        float height = initialHeight;
        if(hovers){
            if(this.startedAnimation == 0){
                this.startedAnimation = tick;
            }
            long tickDifference = tick - this.startedAnimation;
            float increase = easeInFunction.apply(Math.min(increasePerMs * tickDifference, 1));

            width  += maxSize * increase;
            height += maxSize * increase;
            this.endedAnimation = tick;
        }else{
            if(this.endedAnimation != 0){
                long tickDifference = tick - this.endedAnimation;
                float increase = easeOutFunction.apply(1.0f - Math.min(increasePerMs * tickDifference, 1));

                if(increase <= 0){
                    this.endedAnimation = 0;
                }

                width  += maxSize * increase;
                height += maxSize * increase;
            }

            this.startedAnimation = 0;
        }

        return new float[]{width, height};
    }
    public Animation(float initialWidth, float initialHeight){
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.endedAnimation = 0;
        this.startedAnimation = 0;
    }
}
