package se.liu.albhe576.project;

public class Bullet extends Entity
{
    private Entity shooter;

    private float yDirection;
    private boolean alive;

    public Bullet(final float x, final float y, final float z, final float width, final float height, final Texture texture,
		  final Bounds bounds, Entity shooter, float yDirection)
    {
	super(x, y, z, width, height, texture, bounds);
	this.yDirection = yDirection;
	this.shooter = shooter;
	this.alive = true;
    }
    private long lastUpdate;

    @Override public void update() {
	if(lastUpdate + 10 <= System.currentTimeMillis()){
	    lastUpdate = System.currentTimeMillis();
	    this.y += yDirection;
	}
    }

    @Override public boolean isInScene() {
	if(this.y < -1.1f || !this.alive){
	    return false;
	}
	return true;
    }
}
