package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;

public class Texture
{
    public static final int PLAYER_MODEL = 0;
    public static final int PLAYER_BULLET = 1;
    public static final int ENEMY_MODEL_1 = 2;
    public static final int ENEMY_MODEL_2 = 3;
    public static final int ENEMY_MODEL_3 = 4;
    public static final int BOSS_MODEL_1 = 5;
    public static final int ENEMY_BULLET_1 = 6;
    public static final int ENEMY_BULLET_2 = 7;
    public static final int ENEMY_BULLET_3 = 8;
    public static final int ENEMY_BULLET_4 = 9;
    public static final int BACKGROUND_METEOR = 10;
    public static final int HP_HEART = 11;
    public static final int GREY_BUTTON_02 = 12;
    public static final int GREY_BOX = 13;
    public static final int GREY_CHECKMARK_GREY = 14;
    public static final int GREY_SLIDER_UP = 15;
    public static final int GREY_SLIDER_HORIZONTAL = 16;
    private final int width;
    private final int height;
    private final ByteBuffer data;
    public int textureId;
    public int vertexArrayId;
    public int getWidth(){
	return this.width;
    }
    public int getHeight(){
	return this.height;
    }
    public ByteBuffer getData(){
	return this.data;
    }

    public Texture(int width, int height,ByteBuffer data){
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static Texture createTokenTexture(){
        // 100 x 100 rgba
        ByteBuffer buffer = BufferUtils.createByteBuffer(100 * 100 * 4);
        byte[] data = new byte[100 * 100 * 4];
        for(int i = 0; i < data.length; i+=4){
            data[i + 0] = (byte)255;
            data[i + 1] = (byte)0;
            data[i + 2] = (byte)0;
            data[i + 3] = (byte)255;
        }
        buffer.put(data);
        buffer.flip();
        return new Texture(100, 100, buffer);

    }
}
