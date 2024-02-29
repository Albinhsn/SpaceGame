package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

public class Background {

    private final List<Entity> meteors;

    public List<Entity> getMeteors(){
        return this.meteors;
    }
    public Background(){
        this.meteors = new ArrayList<>();
    }

    private boolean entityIsOutOfBounds(Entity entity){
        return true;
    }

    public void updateBackground(){
        for(Entity entity : this.meteors){
            if(this.entityIsOutOfBounds(entity)){

            }
        }

    }
}
