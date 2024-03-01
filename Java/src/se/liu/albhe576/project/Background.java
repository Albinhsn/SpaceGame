package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Background {

    private final List<Meteor> meteors;

    public List<Entity> getMeteors(){
        return this.meteors.stream().map(meteor -> (Entity)meteor).collect(Collectors.toList());
    }

    final Random rng = new Random();

    private float getRandomMeteorX(){
        return rng.nextFloat(-Game.SCREEN_WIDTH, Game.SCREEN_WIDTH);
    }
    private float getRandomMeteorWidth(){
        return rng.nextFloat(1.5f, 7.5f);
    }
    private float getRandomMeteorHeight(){
        return rng.nextFloat(1.5f, 7.5f);
    }
    private float getRandomMeteorAcceleration(){
        return rng.nextFloat(1.0f, 8.0f);
    }
    private float getRandomMeteorY(){
        return rng.nextFloat(Game.SCREEN_HEIGHT, Game.SCREEN_HEIGHT * 1.1f);
    }
    private void updateMeteor(Entity meteor){
        meteor.x = this.getRandomMeteorX();
        meteor.y = this.getRandomMeteorY();
        meteor.width = this.getRandomMeteorWidth();
        meteor.height = meteor.width;
    }
    private void initMeteors(){
        final int numberOfMeteors = 15;
        final int textureIdx = 6;
        for(int i = 0; i < numberOfMeteors; i++){
            float x = this.getRandomMeteorX();
            float y = this.getRandomMeteorY();
            float height = this.getRandomMeteorHeight();
            float width = height;
            float yAcceleration = this.getRandomMeteorAcceleration();

            this.meteors.add(new Meteor(x,y,width ,height , textureIdx, yAcceleration));
        }

    }
    public Background(){
        this.meteors = new ArrayList<>();
        this.initMeteors();
    }

    private boolean entityIsOutOfBounds(Entity entity){
        return entity.y - entity.height - 2.0f < -Game.SCREEN_HEIGHT;
    }

    public void update(long startTime){
        for(Meteor meteor : this.meteors){
            if(this.entityIsOutOfBounds(meteor)){
                this.updateMeteor(meteor);
            }
            meteor.update(startTime);
        }

    }
}
