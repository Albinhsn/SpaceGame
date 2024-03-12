package se.liu.albhe576.project;

import static org.lwjgl.glfw.GLFW.*;

public class Player  extends Entity{

    private long lastShot;
	private final int initialHp;

    public Player(int hp, float x, float y, float width, float height, int textureIdx){
		super(hp, x, y, width, height, textureIdx, 0.0f,  ResourceManager.STATE_VARIABLES.getOrDefault("playerMS", 1.0f));
			this.lastShot 	= 0;
			this.initialHp 	= hp;
    }

	public void reset(){
		this.health = initialHp;
		this.x 			= 0;
		this.y 			= 0;
		this.lastShot 	= 0;
	}
	@Override public float getMovementSpeed(){
		return ResourceManager.STATE_VARIABLES.getOrDefault("playerMS", super.getMovementSpeed());
	}

	public boolean updatePlayer(InputState inputState, long lastTick){
		float xAcceleration 	= 0;
		float yAcceleration 	= 0;
		float moveSpeed = this.getMovementSpeed();

		if(inputState.isKeyPressed(GLFW_KEY_W)){
			yAcceleration += moveSpeed;
		}
		if(inputState.isKeyPressed(GLFW_KEY_A)){
			xAcceleration -= moveSpeed;
		}

		if(inputState.isKeyPressed(GLFW_KEY_S)){
			yAcceleration -= moveSpeed;
		}
		if(inputState.isKeyPressed(GLFW_KEY_D)){
			xAcceleration += moveSpeed;
		}

		this.x += xAcceleration;
		this.y += yAcceleration;
		if(!isWithinScreen()){
			this.x -= xAcceleration;
			this.y -= yAcceleration;
		}

		// Check whether we shoot or not
		return inputState.isKeyPressed(GLFW_KEY_SPACE) && this.canShoot(lastTick);
	}
	@Override
	protected boolean takeDamage(){
		if(ResourceManager.STATE_VARIABLES.getOrDefault("godmode", 0.0f).intValue() != 1){
			this.health -= 1;
			return this.health <= 0;
		}
		return false;
	}
	private long getGCD(){
		return ResourceManager.STATE_VARIABLES.getOrDefault("playerGCDMS", 500.0f).longValue();
	}
    private boolean canShoot(long lastTick){
		if(lastShot > lastTick){
			return false;
		}
		this.lastShot = lastTick + this.getGCD();
		return true;
    }

}
