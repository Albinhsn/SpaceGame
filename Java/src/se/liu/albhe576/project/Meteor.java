package se.liu.albhe576.project;


public class Meteor extends Entity{

    private final float yAcceleration;
    public Meteor(float x, float y, float width, float height, int textureIdx, float yAcceleration) {
        super(0, x, y, width, height, textureIdx, (float) (Math.random() / 360.0f));
        this.yAcceleration = yAcceleration;
    }

    public void update() {
        this.y -= this.yAcceleration;
    }
}
