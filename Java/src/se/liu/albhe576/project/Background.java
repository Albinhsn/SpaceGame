package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 */
public class Background {
    /**
     *
     */
    static class Meteor extends Entity{

        /**
         * @param x
         * @param y
         * @param width
         * @param height
         * @param textureIdx
         * @param yAcceleration
         */
        public Meteor(float x, float y, float width, float height, int textureIdx, float yAcceleration) {
            super(0, x, y, width, height, textureIdx, (float) (Math.random() / 360.0f), 0);
            this.yAcceleration = yAcceleration;
        }

        /**
         *
         */
        public void update() {
            this.y -= this.yAcceleration;
        }
    }

    /**
     *
     */
    private final List<Meteor> meteors;
    /**
     *
     */
    private final Timer timer;
    /**
     *
     */
    private long lastUpdate;

    /**
     * @param renderer
     */
    public void updateAndRender(Renderer renderer){
        this.timer.updateTimer();
        long tick = this.timer.getLastTick();

        if(this.lastUpdate + 16 <= tick){
            for(Meteor meteor : this.meteors){
                if(this.meteorIsOutOfBounds(meteor)){
                    this.resetMeteor(meteor);
                }
                meteor.update();
            }

            this.lastUpdate = tick;
        }
        renderer.renderEntities(this.meteors);
    }

    /**
     *
     */
    private final Random rng = new Random();

    /**
     * @return
     */
    private float getRandomMeteorX(){
        return rng.nextFloat(-100.0f, 100.0f);
    }

    /**
     * @return
     */
    private float getRandomMeteorWidth(){
        final float lowerBound = 0.25f;
        final float upperBound = 0.65f;
        return rng.nextFloat(lowerBound, upperBound);
    }

    /**
     * @return
     */
    private float getRandomMeteorHeight(){
        final float lowerBound = 0.25f;
        final float upperBound = 0.65f;
        return rng.nextFloat(lowerBound, upperBound);
    }

    /**
     * @return
     */
    private float getRandomMeteorAcceleration(){
        final float lowerBound = 0.2f;
        final float upperBound = 1.65f;

        return rng.nextFloat(lowerBound, upperBound);
    }

    /**
     * @return
     */
    private float getRandomMeteorY(){
        return rng.nextFloat(100.0f,  110.0f);
    }

    /**
     * @param meteor
     */
    private void resetMeteor(Entity meteor){
        meteor.x = this.getRandomMeteorX();
        meteor.y = this.getRandomMeteorY();
        meteor.width = this.getRandomMeteorWidth();
        meteor.height = meteor.width;
    }

    /**
     *
     */
    private void initMeteors(){
        final int numberOfMeteors = 30;
        final int textureIdx = Texture.BACKGROUND_METEOR;

        for(int i = 0; i < numberOfMeteors; i++){
            float x = this.getRandomMeteorX();
            float y = this.getRandomMeteorY();
            float height = this.getRandomMeteorHeight();
            float width = height;
            float yAcceleration = this.getRandomMeteorAcceleration();

            this.meteors.add(new Meteor(x,y,width ,height , textureIdx, yAcceleration));
        }
    }

    /**
     *
     */
    public Background(){
        this.meteors    = new ArrayList<>();
        this.initMeteors();
        this.timer      = new Timer();
        this.timer.startTimer();
        this.lastUpdate = 0;
    }

    /**
     * @param entity
     * @return
     */
    private boolean meteorIsOutOfBounds(Entity entity){
        return entity.y - entity.height < -100.0f;
    }
}
