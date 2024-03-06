package se.liu.albhe576.project;

import java.util.function.UnaryOperator;

/**
 *
 */
public class Animation {
    /**
     *
     */
    private final float initialWidth;
    /**
     *
     */
    private final float initialHeight;
    /**
     *
     */
    private long        startedAnimation;
    /**
     *
     */
    private long        endedAnimation;

    /**
     * Easing function out cubic
     */
    public static final UnaryOperator<Float> easeOutCubic = (x) -> (float) (1 - Math.pow(1 - x, 3));
    /**
     * Linear easing function
     */
    public static final UnaryOperator<Float> easeLinearly = (x) -> x;
    /**
     * Easing function in cubic
     */
    public static final UnaryOperator<Float> easeInCubic = (x) -> x * x * x;

    /**
     * Animate in by given easing function
     * @param increasePerMs
     * The size increase per ms
     * @param maxSize
     * The maximum size increase
     * @param function
     * The easing function to animate with
     * @return new width and height based on animation
     */
    private float[] animateIn(float increasePerMs, float maxSize, UnaryOperator<Float>function){
        long tick = System.currentTimeMillis();
        if(this.startedAnimation == 0){
            this.startedAnimation = tick;
        }
        long tickDifference = tick - this.startedAnimation;
        float increase = function.apply(Math.min(increasePerMs * tickDifference, 1));

        float width  = this.initialWidth + maxSize * increase;
        float height = this.initialHeight + maxSize * increase;
        this.endedAnimation = tick;

        return new float[]{width, height};

    }

    /**
     * Animate out by given easing function
     * @param increasePerMs
     * The size increase per ms
     * @param maxSize
     * The maximum size increase
     * @param function
     * The easing function to animate with
     * @return new width and height based on animation
     */
    private float[]animateOut(float increasePerMs, float maxSize, UnaryOperator<Float> function){
        long tick = System.currentTimeMillis();
        float width     = this.initialWidth;
        float height    = this.initialHeight;
        if(this.endedAnimation != 0){
            long tickDifference = tick - this.endedAnimation;
            float increase = function.apply(1.0f - Math.min(increasePerMs * tickDifference, 1));

            if(increase <= 0){
                this.endedAnimation = 0;
            }

            width  += maxSize * increase;
            height += maxSize * increase;
        }

        this.startedAnimation = 0;
        return new float[]{width, height};
    }

    /**
     * @param hovers
     * @param increasePerMs
     * @param maxSize
     * @param easeInFunction
     * @param easeOutFunction
     * @return
     */
    public float[] animate(boolean hovers, float increasePerMs, float maxSize, UnaryOperator<Float> easeInFunction, UnaryOperator<Float> easeOutFunction){
        return hovers ? animateIn(increasePerMs, maxSize, easeInFunction) : animateOut(increasePerMs, maxSize, easeOutFunction);
    }

    /**
     * @param initialWidth
     * @param initialHeight
     */
    public Animation(float initialWidth, float initialHeight){
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        this.endedAnimation = 0;
        this.startedAnimation = 0;
    }
}
