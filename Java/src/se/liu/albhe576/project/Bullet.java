package se.liu.albhe576.project;

public class Bullet extends Entity
{
    private final Entity    parent;
    private final IAccelerationFunction yIAccelerationFunction;
    private final IAccelerationFunction xIAccelerationFunction;
    public Entity getParent(){
        return this.parent;
    }

    public void update(long lastTick) {
        float xOffset = xIAccelerationFunction.apply(lastTick, this);
        float yOffset = yIAccelerationFunction.apply(lastTick, this);
        this.y -= yOffset;
        this.x += xOffset;
    }
    @Override
    public boolean checkCollision(Entity entity)
    {
        if(entity.isWithinScreen() && this.collided(entity) && (this.parent.getClass() != entity.getClass())){
            this.takeDamage();
            return entity.takeDamage();
        }
        return false;
    }

    public Bullet(float x, float y, float width, float height, int textureIdx, Entity parent, float rotation, IAccelerationFunction xIAccelerationFunction, IAccelerationFunction yIAccelerationFunction, float movementSpeed)
    {
        super(0, x, y, width, height, textureIdx, rotation, 0, movementSpeed);
        this.parent = parent;
        this.xIAccelerationFunction = xIAccelerationFunction;
        this.yIAccelerationFunction = yIAccelerationFunction;
    }

}
