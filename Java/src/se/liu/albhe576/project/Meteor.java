package se.liu.albhe576.project;

public class Meteor extends Entity{

    private float yAcceleration;
    public Meteor(float x, float y, float width, float height, int textureIdx, float yAcceleration) {
        super(x, y, width, height, textureIdx);
        this.yAcceleration = yAcceleration;
    }

    @Override
    public void update(long startTime) {
        this.y -= this.yAcceleration;
    }
}
