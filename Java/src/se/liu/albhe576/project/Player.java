package se.liu.albhe576.project;

public class Player  extends Entity{

    private long lastShot;
	private final int initialHp;

    public Player(int hp, float x, float y, float width, float height, int textureIdx){
		super(hp, x, y, width, height, textureIdx, 0.0f);
			this.lastShot 	= 0;
			this.initialHp 	= hp;
    }

	public void reset(){
		this.alive 		= true;
		this.hp 		= initialHp;
		this.x 			= 0;
		this.y 			= 0;
		this.lastShot 	= 0;
	}

	public boolean updatePlayer(InputState inputState, long lastTick){
		final float moveSpeed 	= 1.0f;
		float xAcceleration 	= 0;
		float yAcceleration 	= 0;

		if(inputState.isWPressed()){
			yAcceleration += moveSpeed;
		}
		if(inputState.isAPressed()){
			xAcceleration -= moveSpeed;
		}

		if(inputState.isSPressed()){
			yAcceleration -= moveSpeed;
		}
		if(inputState.isDPressed()){
			xAcceleration += moveSpeed;
		}

		this.x += xAcceleration;
		this.y += yAcceleration;
		if(!isWithinBounds()){
			this.x -= xAcceleration;
			this.y -= yAcceleration;
		}

		return inputState.isSpacePressed() && this.canShoot(lastTick);
	}

    private boolean canShoot(long lastTick){
		final long gcd = 500;
		if(lastShot > lastTick){
			return false;
		}
		this.lastShot = lastTick + gcd;
		return true;
    }

}
