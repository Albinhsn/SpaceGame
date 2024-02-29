package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private long canShoot;
    public Player(float x, float y, float width, float height, int textureId, int textureUnit){
		super(x, y, width, height, textureId, textureUnit);
			this.lastUpdate = 0;
			this.canShoot = 0;
    }
    @Override public void update(long startTime){
    }

    public Bullet shoot(){
		final long gcd = 300;
		long timer = System.currentTimeMillis();
		if(canShoot > timer){
			return null;
		}
		this.canShoot = timer + gcd;
		return Bullet.createNewPlayerBullet(this);
    }

    private  long lastUpdate;


    private boolean isOutOfBounds(){
		return false;
	}

}
