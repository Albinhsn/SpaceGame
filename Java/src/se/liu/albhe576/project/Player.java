package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private Point direction;
    private long canShoot;
    public Player(final float x, final float y, final float z, final float width, final float height, final Texture texture,
		  final Bounds bounds)
    {
	super(x, y, z, width, height, texture, bounds);
	this.direction = new Point(0, 0);
	this.lastUpdate = 0;
	this.canShoot = 0;
    }
    public Point getDirection(){
	return this.direction;
    }
    public void resetDirection(){
	this.direction.x = 0;
	this.direction.y = 0;
    }
    @Override public void update(long startTime){
	this.move();
	this.resetDirection();
    }

    @Override public boolean isInScene() {
	return true;
    }

    public Bullet shoot(){
	final long gcd = 300;
	long timer = System.currentTimeMillis();
	if(canShoot <= timer){
	    this.canShoot = timer + gcd;
	    Texture texture = null;
	    try{
		texture = GameData.loadPNGFile("./resources/images/PNG/Sprites/Missiles/spaceMissiles_002.png");
	    }catch(IOException e){
		e.printStackTrace();
		System.exit(1);
	    }
	    // ToDo :)
	    return new Bullet(this.x, this.y, this.z, 0.02f, 0.04f, texture, new Bounds(0.02f, 0.04f, 0.0f, 0.0f, Color.BLUE, 10, texture.getWidth(),
											texture.getHeight()), this, -0.006f);
	}
	return null;
    }

    private  long lastUpdate;

    public void move(){
	// ToDo figure out better way to handle this, since we can actually miss some input
	if(lastUpdate + 10 <= System.currentTimeMillis()){
	    lastUpdate = System.currentTimeMillis();
	    final float moveSpeed = 0.004f;
	    this.x += this.direction.x * moveSpeed;
	    this.y += this.direction.y * moveSpeed;
	    this.handleOutOfBounds();
	}
    }

    private void handleOutOfBounds(){
	Bounds bounds = this.getBounds();
	float width = bounds.getWidth();
	float height = bounds.getHeight();

	float yOffset = bounds.getTextureOffsetY();

	float minX = -width + x;
	float maxX = width + x;

	float maxY = height - y + yOffset;
	float minY = -height - y + yOffset;

	if(minX < -1.0f){
	    this.x += -1.0f - minX;
	}else if(maxX > 1.0f){
	    this.x -= maxX - 1.0f;
	}

	if(minY < -1.0f){
	    this.y -= -1.0f - minY;
	}else if(maxY > 1.0f){
	    this.y += maxY - 1.0f;
	}
    }

    public void moveLeft(){
	this.direction.x--;
    }
    public void moveRight(){
	this.direction.x++;
    }
    public void moveUp(){
	this.direction.y--;
    }
    public void moveDown(){
	this.direction.y++;
    }
}
