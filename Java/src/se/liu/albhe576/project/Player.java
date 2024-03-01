package se.liu.albhe576.project;

public class Player  extends Entity{

    public long lastShot;

    public Player(int hp, float x, float y, float width, float height, int textureIdx){
		super(hp, x, y, width, height, textureIdx, 0.0f);
			this.lastShot = 0;
    }

	public boolean updatePlayer(InputState inputState, long lastTick){
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

		return inputState.isSpacePressed() && this.canShoot(lastTick);
	}

    public boolean canShoot(long lastTick){
		final long gcd = 500;
		if(lastShot > lastTick){
			return false;
		}
		this.lastShot = lastTick + gcd;
		return true;
    }

    private boolean isOutOfBounds(){
		final int x = (int) (Game.SCREEN_WIDTH - this.width / 2);
		final int y = (int) (Game.SCREEN_HEIGHT - this.height / 2);
		return this.x <= -x || this.x >= x || this.y <= -y || this.y >= y;
	}

}
