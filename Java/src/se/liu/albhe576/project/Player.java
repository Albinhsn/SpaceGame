package se.liu.albhe576.project;

import static org.lwjgl.glfw.GLFW.*;

public class Player  extends Entity{

    private long lastShot;
	private final int initialHp;

    public Player(int hp, float x, float y, float width, float height, int textureIdx){
		super(hp, x, y, width, height, textureIdx, 0.0f, 0, ResourceManager.STATE_VARIABLES.getOrDefault("playerMS", 1.0f));
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
		if(ResourceManager.STATE_VARIABLES.getOrDefault("invincible", 0.0f).intValue() != 1){
			this.hp -= 1;
			boolean dead = this.hp <= 0;
			this.alive = !dead;
			return dead;
		}
		return false;
	}

    private boolean canShoot(long lastTick){
		if(lastShot > lastTick){
			return false;
		}
		final long gcd = ResourceManager.STATE_VARIABLES.getOrDefault("playerGCDMS", 500.0f).longValue();
		this.lastShot = lastTick + gcd;
		return true;
    }

}
