package se.liu.albhe576.project;

import java.awt.*;
import java.nio.ByteBuffer;

public class Texture
{
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
}
