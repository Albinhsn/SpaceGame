package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private long canShoot;
	private float xAcc;
	private float yAcc;
    public Player(float x, float y, float width, float height, int textureIdx){
		super(x, y, width, height, textureIdx);
			this.lastUpdate = 0;
			this.canShoot = 0;
    }
    @Override public void update(long startTime){
    }

	public void updatePlayerAcceleration(InputState inputState){

		final float moveSpeed = 5.0f;
		this.xAcc = 0;
		this.yAcc = 0;
		if(inputState.isWPressed()){
			this.yAcc += moveSpeed;
		}
		if(inputState.isAPressed()){
			this.xAcc -= moveSpeed;
		}
		if(inputState.isSPressed()){
			this.yAcc -= moveSpeed;
		}
		if(inputState.isDPressed()){
			this.xAcc += moveSpeed;
		}
		this.x += xAcc;
		this.y += yAcc;
		if(isOutOfBounds()){
			this.x -= xAcc;
			this.y -= yAcc;
			System.out.println(this.x + " " + this.y);
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

    private  long lastUpdate;


    private boolean isOutOfBounds(){
		final int x = (int) (Game.SCREEN_WIDTH - this.width / 2);
		final int y = (int) (Game.SCREEN_HEIGHT - this.height / 2);
		return this.x <= -x || this.x >= x || this.y <= -y || this.y >= y;
	}

}
