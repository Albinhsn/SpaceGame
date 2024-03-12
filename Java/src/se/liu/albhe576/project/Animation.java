package se.liu.albhe576.project;

import java.util.function.UnaryOperator;

public class Animation {
    private final float initialWidth;
    private final float initialHeight;
    private long        startedAnimation;
    private long        endedAnimation;
    private final float maxAddedSize;
    private final long        animationTimer;
    private final UnaryOperator<Float> inFunction;
    private final UnaryOperator<Float> outFunction;
    public static final UnaryOperator<Float> easeOutCubic = (x) -> (float) (1 - Math.pow(1 - x, 3));
    public static final UnaryOperator<Float> easeLinearly = (x) -> x;
    public static final UnaryOperator<Float> easeInCubic = (x) -> x * x * x;

    public float[] animateIn(){
        long tick = System.currentTimeMillis();
        this.endedAnimation = tick;
        if(this.startedAnimation == 0){
            this.startedAnimation = tick;
        }

        long tickDifference = tick - this.startedAnimation;
        float increasePerMs = this.maxAddedSize / (float)this.animationTimer;
        float increase      = this.inFunction.apply(Math.min(increasePerMs * tickDifference, 1));

        float width         = this.initialWidth + this.maxAddedSize * increase;
        float height        = this.initialHeight + this.maxAddedSize * increase;

        return new float[]{width, height};
    }

    public float[]animateOut(){
        long tickDifference     = System.currentTimeMillis() - this.endedAnimation;
        float increasePerMs     = this.maxAddedSize / (float)this.animationTimer;
        float increase          = this.outFunction.apply(1.0f - Math.min(increasePerMs * tickDifference, 1));
        this.startedAnimation   = 0;
        float width             = this.initialWidth  + maxAddedSize * increase;
        float height            = this.initialHeight + maxAddedSize * increase;
        return new float[]{width, height};
    }


    public Animation(float initialWidth, float initialHeight, long animationTimer, float maxAddedSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        this.initialWidth       = initialWidth;
        this.initialHeight      = initialHeight;
        this.endedAnimation     = 0;
        this.startedAnimation   = 0;
        this.animationTimer     = animationTimer;
        this.maxAddedSize       = maxAddedSize;
        this.inFunction         = easeInFunction;
        this.outFunction        = easeOutFunction;
    }
    public Animation(float initialWidth, float initialHeight, long animationTimer, float maxAddedSize, UnaryOperator<Float> easeInFunction){
        this(initialWidth, initialHeight, animationTimer, maxAddedSize, easeInFunction, easeInFunction);
    }
}
