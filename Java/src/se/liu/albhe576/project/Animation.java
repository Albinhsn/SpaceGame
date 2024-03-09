package se.liu.albhe576.project;

import java.util.function.UnaryOperator;

/**
 *
 */
public class Animation {
    private final float initialWidth;
    private final float initialHeight;
    private long        startedAnimation;
    private long        endedAnimation;
    private final float       maxSize;
    private final long        animationTimer;
    private final UnaryOperator<Float> inFunction;
    private final UnaryOperator<Float> outFunction;
    public static final UnaryOperator<Float> easeOutCubic = (x) -> (float) (1 - Math.pow(1 - x, 3));
    public static final UnaryOperator<Float> easeLinearly = (x) -> x;
    public static final UnaryOperator<Float> easeInCubic = (x) -> x * x * x;

    private float[] animateIn(){
        long tick = System.currentTimeMillis();
        if(this.startedAnimation == 0){
            this.startedAnimation = tick;
        }

        long tickDifference = tick - this.startedAnimation;
        float increasePerMs = this.maxSize / (float)this.animationTimer;
        float increase = this.inFunction.apply(Math.min(increasePerMs * tickDifference, 1));

        float width  = this.initialWidth + this.maxSize * increase;
        float height = this.initialHeight + this.maxSize * increase;
        this.endedAnimation = tick;

        return new float[]{width, height};

    }

    private float[]animateOut(){
        long tickDifference = System.currentTimeMillis() - this.endedAnimation;
        float increasePerMs = this.maxSize / (float)this.animationTimer;
        float increase = this.outFunction.apply(1.0f - Math.min(increasePerMs * tickDifference, 1));

        this.startedAnimation = 0;
        float width  = this.initialWidth  + maxSize * increase;
        float height = this.initialHeight + maxSize * increase;
        return new float[]{width, height};
    }

    public float[] animate(boolean hovers){
        return hovers ? animateIn() : animateOut();
    }

    public Animation(float initialWidth, float initialHeight, long animationTimer, float maxSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        this.initialWidth       = initialWidth;
        this.initialHeight      = initialHeight;
        this.endedAnimation     = 0;
        this.startedAnimation   = 0;
        this.animationTimer     = animationTimer;
        this.maxSize            = maxSize;
        this.inFunction         = easeInFunction;
        this.outFunction        = easeOutFunction;
    }
    public Animation(float initialWidth, float initialHeight, long animationTimer, float maxSize, UnaryOperator<Float> easeInFunction){
        this(initialWidth, initialHeight, animationTimer, maxSize, easeInFunction, easeInFunction);
    }
}
