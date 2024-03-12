package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Background {
    static class Meteor extends Entity{
        public Meteor(float x, float y, float width, float height, int textureIdx, float movementSpeed) {
            super(0, x, y, width, height, textureIdx, 0.0f, movementSpeed);
        }
    }
    private List<Meteor> meteors;
    private final Timer timer;
    private long lastUpdate;
    private final Random rng = new Random();
    private void updateNumberOfMeteors(int numberOfMeteors){
        if(numberOfMeteors > this.meteors.size()){
            for(int i = this.meteors.size(); i < numberOfMeteors; i++){
                this.meteors.add(this.createRandomMeteor());
            }
        }else if(numberOfMeteors < this.meteors.size()){
            this.meteors = this.meteors.subList(0, numberOfMeteors);

        }
    }

    public void updateAndRender(Renderer renderer){
        final int numberOfMeteors = ResourceManager.STATE_VARIABLES.getOrDefault("numberOfMeteors", 100.0f).intValue();
        this.updateNumberOfMeteors(numberOfMeteors);

        this.timer.updateTimer();
        long tick = this.timer.getLastTick();

        if(this.lastUpdate + ResourceManager.STATE_VARIABLES.getOrDefault("updateTimerMS", 16.0f) <= tick){
            for(Meteor meteor : this.meteors){
                if(!meteor.isWithinScreen()){
                    this.resetMeteor(meteor);
                }
                meteor.y -= meteor.getMovementSpeed();
            }
            this.lastUpdate = tick;
        }
        renderer.renderEntities(this.meteors);
    }

    private float getRandomFloatRange(float lowerBound, float upperBound){return rng.nextFloat(lowerBound, upperBound);}
    private void resetMeteor(Entity meteor){
        meteor.x        = this.getRandomFloatRange(-100.0f, 100.0f);
        meteor.y        = this.getRandomFloatRange(100.0f, 110.0f);
        meteor.width    = this.getRandomFloatRange(0.25f, 0.65f);
        meteor.height   = meteor.width;
    }
    private Meteor createRandomMeteor(){
        final int textureIdx    = Texture.BACKGROUND_METEOR;
        float x                 = this.getRandomFloatRange(-100.f, 100.0f);
        float y                 = this.getRandomFloatRange(-100.0f, 100.0f);
        float height            = this.getRandomFloatRange(0.25f, 0.65f);
        float width             = height;
        float movementSpeed     = this.getRandomFloatRange(0.25f, 1.65f);

        return new Meteor(x,y,width,height,textureIdx, movementSpeed);
    }
    private void initMeteors(){
        final int numberOfMeteors = ResourceManager.STATE_VARIABLES.getOrDefault("numberOfMeteors", 100.0f).intValue();
        for(int i = 0; i < numberOfMeteors; i++){
            this.meteors.add(this.createRandomMeteor());
        }
    }
    public Background(){
        this.meteors    = new ArrayList<>();
        this.timer      = new Timer();
        this.lastUpdate = 0;

        this.initMeteors();
        this.timer.startTimer();
    }

}
