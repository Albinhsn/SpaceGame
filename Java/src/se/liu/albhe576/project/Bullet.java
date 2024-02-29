package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bullet extends Entity
{
    private Entity parent;
    final float yAcc;

    public Bullet(
	    float x,
	    float y,
        float width,
        float height,
        int textureIdx,
	    Entity shooter,
        float yAcc
    )
    {
		super(x, y, width, height, textureIdx);
        System.out.printf("Creating bullet width idx: %d\n", textureIdx);
		this.parent = shooter;
        this.lastUpdate = System.currentTimeMillis();
        this.yAcc = yAcc;
    }
    private long lastUpdate;

    @Override public void update(long startTime) {
		if(lastUpdate + 10 <= System.currentTimeMillis()){
			lastUpdate = System.currentTimeMillis();
			this.y += this.yAcc;
		}
    }

    public void checkCollision(List<Entity> entities){}
}
