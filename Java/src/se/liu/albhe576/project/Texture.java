package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
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
    public static final int GREY_BUTTON_05 = 12;
    public static final int GREY_BOX = 13;
    public static final int GREY_CHECKMARK_GREY = 14;
    public static final int GREY_SLIDER_UP = 15;
    public static final int GREY_SLIDER_HORIZONTAL = 16;
    public static final int GREY_BUTTON_14 = 17;
    private final int width;
    private final int height;
    private final ByteBuffer data;
    public int textureId;
    public int getWidth(){
	return this.width;
    }
    public int getHeight(){
	return this.height;
    }
    public ByteBuffer getData(){
	return this.data;
    }

    public static Texture loadPNGFile(String fileLocation) throws IOException {
        File file = new File(fileLocation);
        BufferedImage image = ImageIO.read(file);

        Raster raster = image.getData();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        byte [] bytes = data.getData();
        ByteBuffer buffer = null;

        ColorModel model = image.getColorModel();
        int pixelSize = model.getPixelSize();
        switch(pixelSize){
            // Black or white
            case 4:{
                buffer = BufferUtils.createByteBuffer(bytes.length * 4);
                for(int i = 0, idx = 0; i < bytes.length; i++, idx += 4){
                    buffer.put(idx + 0, bytes[i]);
                    buffer.put(idx + 1, bytes[i]);
                    buffer.put(idx + 2, bytes[i]);
                    buffer.put(idx + 3, (byte)0xFF);
                }
                break;
            }
            // Grayscale
            case 8:{
                // ToDo, figure out how to shift this another way
                buffer = BufferUtils.createByteBuffer(bytes.length * 4);
                for(int i = 0, idx = 0; i < bytes.length; i++, idx += 4){
                    byte a = bytes[i];
                    byte val = (byte)(a * 4);
                    buffer.put(idx + 0, val);
                    buffer.put(idx + 1, val);
                    buffer.put(idx + 2, val);
                    buffer.put(idx + 3, val > 80 ? (byte)0xFF : (byte)0);
                }
                break;
            }
            // RGBA
            case 32:{
                buffer = BufferUtils.createByteBuffer(bytes.length);
                for(int i = 0; i < bytes.length; i+=4){
                    byte a = bytes[i];
                    byte r = bytes[i + 1];
                    byte g = bytes[i + 2];
                    byte b = bytes[i + 3];
                    buffer.put(i + 3, a);
                    buffer.put(i + 0, b);
                    buffer.put( i + 1, g);
                    buffer.put(i + 2, r);
                }
                break;
            }
            default:{
                System.out.println("Don't know how to load png with bpp of " + pixelSize);
                System.exit(1);
            }

        }
        buffer.flip();

        return new Texture(image.getWidth(), image.getHeight(), buffer);

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
