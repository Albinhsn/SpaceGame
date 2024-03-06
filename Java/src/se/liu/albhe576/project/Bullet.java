package se.liu.albhe576.project;

/**
 *
 */
public class Bullet extends Entity
{
    /**
     *
     */
    private final Entity    parent;
    /**
     *
     */
    private final float     yAcc;

    /**
     * @return
     */
    public Entity getParent(){
        return this.parent;
    }

    /**
     *
     */
    public void update() {
        this.y += this.yAcc;
    }


    /**
     * @param entity
     * @return
     */
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

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param textureIdx
     * @param shooter
     * @param yAcc
     * @param rotation
     */
    public Bullet(float x, float y, float width, float height, int textureIdx, Entity shooter, float yAcc, float rotation)
    {
        super(0, x, y, width, height, textureIdx, rotation, 0);
        this.parent = shooter;
        this.yAcc = yAcc;
    }
}
