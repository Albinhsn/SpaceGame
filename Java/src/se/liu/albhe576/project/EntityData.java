package se.liu.albhe576.project;

public class EntityData {
    public static final int size = 10 * 4;
    int hp;
    int textureIdx;
    float width;
    float height;
    int bulletTextureIdx;
    float bulletSpeed;
    float bulletWidth;
    float bulletHeight;
    int score;
    float movementSpeed;
    public EntityData(int hp, int ti, float w, float h, int bti, float bs, float bw, float bh, int s, float ms){
        this.hp 				= hp;
        this.textureIdx 		= ti;
        this.width 				= w;
        this.height 			= h;
        this.bulletTextureIdx 	= bti;
        this.bulletSpeed 		= bs;
        this.bulletWidth 		= bw;
        this.bulletHeight 		= bh;
        this.score 				= s;
        this.movementSpeed 		= ms;
    }
}
