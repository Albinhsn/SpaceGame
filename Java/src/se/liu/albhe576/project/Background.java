package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Background {
    static class Meteor extends Entity{

        public Meteor(float x, float y, float width, float height, int textureIdx, float movementSpeed) {
            super(0, x, y, width, height, textureIdx, (float) (Math.random() / 360.0f), 0,movementSpeed);
        }
        public void update() {
            this.y -= this.getMovementSpeed();
        }
    }

    private List<Meteor> meteors;
    private final Timer timer;
    private long lastUpdate;

    private final Random rng = new Random();
    private void updateNumberOfMeteors(int numberOfMeteors){
        if(numberOfMeteors > this.meteors.size()){
            int prevSize = this.meteors.size();
            for(int i = this.meteors.size(); i < numberOfMeteors; i++){
                this.meteors.add(this.createRandomMeteor());
            }
            System.out.printf("Added %d more\n", numberOfMeteors - prevSize);
        }else if(numberOfMeteors < this.meteors.size()){
            this.meteors = this.meteors.subList(0, numberOfMeteors);

        }
    }

    public void updateAndRender(Renderer renderer){
        final int numberOfMeteors = ResourceManager.STATE_VARIABLES.get("numberOfMeteors").intValue();
        this.updateNumberOfMeteors(numberOfMeteors);

        this.timer.updateTimer();
        long tick = this.timer.getLastTick();

        if(this.lastUpdate + 16 <= tick){
            for(Meteor meteor : this.meteors){
                if(!meteor.isWithinScreen()){
                    this.resetMeteor(meteor);
                }
                meteor.update();
            }

            this.lastUpdate = tick;
        }
        renderer.renderEntities(this.meteors);
    }

    private float getRandomMeteorX(){
        return rng.nextFloat(-100.0f, 100.0f);
    }
    private float getRandomMeteorWidth(){
        final float lowerBound = 0.25f;
        final float upperBound = 0.65f;
        return rng.nextFloat(lowerBound, upperBound);
    }
    private float getRandomMeteorHeight(){
        final float lowerBound = 0.25f;
        final float upperBound = 0.65f;
        return rng.nextFloat(lowerBound, upperBound);
    }
    private float getRandomMeteorAcceleration(){
        final float lowerBound = 0.2f;
        final float upperBound = 1.65f;

        return rng.nextFloat(lowerBound, upperBound);
    }
    private float getRandomMeteorY(){
        return rng.nextFloat(100.0f,  110.0f);
    }
    private void resetMeteor(Entity meteor){
        meteor.x = this.getRandomMeteorX();
        meteor.y = this.getRandomMeteorY();
        meteor.width = this.getRandomMeteorWidth();
        meteor.height = meteor.width;
    }
    private Meteor createRandomMeteor(){
        final int textureIdx = Texture.BACKGROUND_METEOR;
        float x = this.getRandomMeteorX();
        float y = this.getRandomMeteorY();
        float height = this.getRandomMeteorHeight();
        float width = height;
        float movementSpeed = this.getRandomMeteorAcceleration();

        return new Meteor(x,y,width,height,textureIdx, movementSpeed);
    }
    private void initMeteors(){
        final int numberOfMeteors = ResourceManager.STATE_VARIABLES.get("numberOfMeteors").intValue();

        for(int i = 0; i < numberOfMeteors; i++){
            this.meteors.add(this.createRandomMeteor());
        }
    }
    public Background(){
        this.meteors    = new ArrayList<>();
        this.initMeteors();
        this.timer      = new Timer();
        this.timer.startTimer();
        this.lastUpdate = 0;
    }

}
