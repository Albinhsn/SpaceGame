package se.liu.albhe576.project;

import java.awt.*;
import java.io.IOException;

public class Player  extends Entity{

    private Point direction;
    private long canShoot;
    public Player(final int x, final int y, final float textureWidth, final float textureHeight, final Texture texture,final Bounds bounds)
    {
	super(x, y, textureWidth, textureHeight, texture, bounds);
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
	    return new Bullet(
		    this.x,
		    this.y - (int)(500* ((0.03f + this.getTextureHeight()) / 2.0f)),
		   0.03f,
		    0.03f,
		    texture,
		   new Bounds(
			   0.04f,
			   0.04f,
			   0.0f,
			   0.0f,
			   Color.BLUE,
			   1,
			   texture.getWidth(),
			   texture.getHeight()),
		    this,
		    -3
	    );
	}
	return null;
    }

    private  long lastUpdate;

    public void move(){
	// ToDo figure out better way to handle this, since we can actually miss some input
	if(lastUpdate + 10 <= System.currentTimeMillis()){
	    lastUpdate = System.currentTimeMillis();
	    final int moveSpeed = 3;
	    this.x += this.direction.x * moveSpeed;
	    this.y += this.direction.y * moveSpeed;

	    if(this.isOutOfBounds()){
		this.x -= this.direction.x * moveSpeed;
		this.y -= this.direction.y * moveSpeed;
	    }
	}
    }

    private boolean isOutOfBounds(){
	Bounds bounds = this.getBounds();
	float width = bounds.getWidth();
	float height = bounds.getHeight();

	float xOffset = this.x * 0.002f + bounds.getTextureOffsetX();
	float yOffset = this.y * 0.002f + bounds.getTextureOffsetY();

	float minX = -width + xOffset;
	float maxX = width + xOffset;

	float minY = -height + yOffset;
	float maxY = height + yOffset;

	if(minX < -1.0f || maxX > 1.0f || minY < -1.0f || maxY > 1.0f){
	    System.out.printf("x:(%f, %f), y:(%f, %f)\n", minX, maxX, minY, maxY);
	    return true;
	}

	return false;
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
