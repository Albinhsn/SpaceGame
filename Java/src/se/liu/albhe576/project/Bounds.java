package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;

public class Bounds
{
    private float width;
    private float height;
    private Color color;
    private int thickness;

    private ByteBuffer byteBuffer;

    private float textureOffsetX;
    private float textureOffsetY;

    public ByteBuffer getByteBuffer(){
        return this.byteBuffer;
    }

    public Color getColor(){
        return this.color;
    }

    public int getThickness(){
        return this.thickness;
    }

    public float getWidth(){
        return this.width;
    }
    public float getHeight(){
        return this.height;
    }
    public float getTextureOffsetX(){
        return this.textureOffsetX;
    }
    public float getTextureOffsetY(){
        return this.textureOffsetY;
    }

    public Bounds(float width, float height, float xOffset, float yOffset, Color color, int thickness, int textureWidth, int textureHeight){
        this.width = width;
        this.height = height;
        this.textureOffsetX = xOffset;
        this.textureOffsetY = yOffset;
        this.color = color;
        this.thickness = thickness;
        this.byteBuffer = Bounds.getBoundsBuffer(textureWidth, textureHeight, color, thickness);
    }

    public static ByteBuffer getBoundsBuffer(int width, int height, Color color, int thickness){
        final int bpp = 4;
        int cap = width * height * bpp;

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(cap);
        for(int i = 0; i < cap; i++){
            byteBuffer.put((byte)0);
        }
        int bottomRow = (height - thickness) * width * bpp;
        for(int i = 0; i < width * thickness * bpp; i+=bpp){
            byteBuffer.put(i + 0, (byte)color.getRed());
            byteBuffer.put(i + 1, (byte)color.getGreen());
            byteBuffer.put(i + 2, (byte)color.getBlue());
            byteBuffer.put(i + 3, (byte) 0xFF);

            byteBuffer.put(bottomRow + i + 0, (byte)color.getRed());
            byteBuffer.put(bottomRow + i + 1, (byte)color.getGreen());
            byteBuffer.put(bottomRow + i + 2, (byte)color.getBlue());
            byteBuffer.put(bottomRow + i + 3, (byte)color.getAlpha());
        }

        int lastColumn = (width - thickness) * bpp;
        for(int i = 0; i < height; i++){
            int yOffset = i * width * bpp;
            for(int j = 0; j < thickness * bpp; j += 4){
                byteBuffer.put(yOffset + j + 0, (byte)color.getRed());
                byteBuffer.put(yOffset + j + 1, (byte)color.getGreen());
                byteBuffer.put(yOffset + j + 2, (byte)color.getBlue());
                byteBuffer.put(yOffset + j + 3, (byte)color.getAlpha());

                byteBuffer.put(yOffset + j + lastColumn + 0, (byte)color.getRed());
                byteBuffer.put(yOffset + j + lastColumn + 1, (byte)color.getGreen());
                byteBuffer.put(yOffset + j + lastColumn + 2, (byte)color.getBlue());
                byteBuffer.put(yOffset + j + lastColumn + 3, (byte)color.getAlpha());
            }
        }


        byteBuffer.flip();

        return byteBuffer;
    }
}
