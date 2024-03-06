package se.liu.albhe576.project;

import java.util.Random;

public class Enemy extends Entity{
    private final int pathId;
    private final long spawnTime;
    private long lastShot;
    private final float moveSpeed;
   public final int type;

    public boolean hasSpawned(long lastTick){
        return lastTick >= this.spawnTime;
    }
    private boolean willShoot(long lastTick){
        final Random rng        = new Random();
        final long lowerBound   = ResourceManager.STATE_VARIABLES.get("enemyGCDMin").longValue();
        final long upperBound   = ResourceManager.STATE_VARIABLES.get("enemyGCDMax").longValue();

        final long gcd = rng.nextLong(lowerBound, upperBound);
        if(this.lastShot <= lastTick - gcd){
            this.lastShot = lastTick;
            return true;
        }
        return false;

    }

    private void move(long lastTick){
        switch(this.pathId){
            case 0:{
                this.x += (float) Math.sin((double) lastTick / 500) / 5.0f;
                this.y += (float) Math.cos((double) lastTick / 500) / 5.0f + this.moveSpeed;
                break;
            }
            case 1:{
                this.x += (float) Math.sin((double) lastTick / 1000) / 5.0f;
                this.y += this.moveSpeed;
                break;
            } case 2:{
                this.y += (float) Math.sin((double) lastTick / 1000) / 5.0f;
                break;
            }
            // This is boss movement
            case 3:{
                if(this.y >= 50.0f){
                    this.y -= 0.2f;
                }else{
                    this.y += (float) Math.sin((double) lastTick / 1000) / 5.0f;
                }
                this.x             += (float) Math.cos((double) lastTick / 500);
                break;
            }
            default:{
                System.out.printf("How did this happen to me %d\n", this.pathId);
                System.exit(1);
            }
        }
    }
    public boolean update(long lastTick) {
        if(this.hasSpawned(lastTick) && this.alive){
            move(lastTick);
            return willShoot(lastTick);
        }
        return false;
    }
    public Enemy copyEnemy(){
        return new Enemy(
                this.hp,
                this.type,
                this.x,
                this.y,
                this.width,
                this.height,
                this.getTextureIdx(),
                this.spawnTime,
                this.pathId,
                this.scoreGiven,
                this.moveSpeed
        );

    }
    public Enemy(final int hp, final int thisType, final float x, final float y, final float width, final float height, int textureIdx, long spawnTime, int pathId, int scoreGiven, float movementSpeed)
    {
        super(hp, x, y, width, height, textureIdx, 0.0f, scoreGiven);
        this.type           = thisType;
        this.pathId         = pathId;
        this.alive          = true;
        this.spawnTime      = spawnTime;
        this.moveSpeed      = movementSpeed;
        this.lastShot       = 0;
    }
}
