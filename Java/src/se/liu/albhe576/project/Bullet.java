package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    private Entity parent;
    private float yDirection;

    public Bullet(
	    float x,
	    float y,
        float width,
        float height,
        int textureId,
        int textureUnit,
	    Entity shooter
    )
    {
		super(x, y, width, height, textureId, textureUnit);
		this.parent = shooter;
    }
    private long lastUpdate;

    @Override public void update(long startTime) {
		if(lastUpdate + 10 <= System.currentTimeMillis()){
			lastUpdate = System.currentTimeMillis();
			this.y += yDirection;
		}
    }

    public void checkCollision(List<Entity> entities){}
}
