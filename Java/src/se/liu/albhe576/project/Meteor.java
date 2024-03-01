package se.liu.albhe576.project;

import java.util.List;

public class Meteor extends Entity{

    private float yAcceleration;
    public Meteor(float x, float y, float width, float height, int textureIdx, float yAcceleration) {
        super(x, y, width, height, textureIdx, (float) (Math.random() / 360.0f));
        this.yAcceleration = yAcceleration;
    }

    public void update() {
        this.y -= this.yAcceleration;
    }
}
