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
        int textureId,
        int textureUnit,
	    Entity shooter,
        float yAcc
    )
    {
		super(x, y, width, height, textureId, textureUnit);
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

    public static Bullet createNewPlayerBullet(Player player){
        final float yOffset = player.height / 2.0f;
        return new Bullet(
                player.x,
                player.y + yOffset + 10.0f,
                10.0f,
                10.0f,
                0,
                0,
                player,
                3.0f
        );
    }

    public void checkCollision(List<Entity> entities){}
}
