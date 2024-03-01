package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private long canShoot;
    public Player(float x, float y, float width, float height, int textureIdx){
		super(x, y, width, height, textureIdx);
			this.canShoot = 0;
    }
    @Override public void update(long startTime){
    }

	public void updatePlayerAcceleration(InputState inputState){

		final float moveSpeed = 5.0f;
		this.xAcceleration = 0;
		this.yAcceleration = 0;
		if(inputState.isWPressed()){
			this.yAcceleration += moveSpeed;
		}
		if(inputState.isAPressed()){
			this.xAcceleration -= moveSpeed;
		}
		if(inputState.isSPressed()){
			this.yAcceleration -= moveSpeed;
		}
		if(inputState.isDPressed()){
			this.xAcceleration += moveSpeed;
		}
		this.x += xAcceleration;
		this.y += yAcceleration;
		if(isOutOfBounds()){
			this.x -= xAcceleration;
			this.y -= yAcceleration;
		}

	}

    public boolean shoot(){
		final long gcd = 300;
		long timer = System.currentTimeMillis();
		if(canShoot > timer){
			return false;
		}
		this.canShoot = timer + gcd;
		return true;
    }

    private boolean isOutOfBounds(){
		final int x = (int) (Game.SCREEN_WIDTH - this.width / 2);
		final int y = (int) (Game.SCREEN_HEIGHT - this.height / 2);
		return this.x <= -x || this.x >= x || this.y <= -y || this.y >= y;
	}

}
