package se.liu.albhe576.project;

public class Bullet extends Entity
{
    private final Entity    parent;
    private final AccelerationFunction yAccelerationFunction;
    private final AccelerationFunction xAccelerationFunction;
    public Entity getParent(){
        return this.parent;
    }

    public void update(long lastTick) {
        float xOffset = xAccelerationFunction.apply(lastTick, this);
        float yOffset = yAccelerationFunction.apply(lastTick, this);
        this.y -= yOffset;
        this.x += xOffset;
    }


    @Override
    public boolean checkCollision(Entity entity)
    {
        if(!this.isWithinScreen()){
            return false;
        }

        if(entity.isWithinScreen() && this.collided(entity) && (this.parent.getClass() != entity.getClass())){
            this.takeDamage();
            return entity.takeDamage();
        }
        return false;
    }

    public Bullet(float x, float y, float width, float height, int textureIdx, Entity parent, float rotation, AccelerationFunction xAccelerationFunction, AccelerationFunction yAccelerationFunction, float movementSpeed)
    {
        super(0, x, y, width, height, textureIdx, rotation, 0, movementSpeed);
        this.parent = parent;
        this.xAccelerationFunction = xAccelerationFunction;
        this.yAccelerationFunction = yAccelerationFunction;
    }

}
