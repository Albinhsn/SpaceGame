package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private long canShoot;
    public Player(final int x, final int y){
		super(x, y);
			this.lastUpdate = 0;
			this.canShoot = 0;
    }
    @Override public void update(long startTime){
		this.move();
    }

    public Bullet shoot(){
		final long gcd = 300;
		long timer = System.currentTimeMillis();
		if(canShoot <= timer){
			this.canShoot = timer + gcd;
		}
		return null;
    }

    private  long lastUpdate;

    public void move(){}

    private boolean isOutOfBounds(){
		return false;
	}

}
