package se.liu.albhe576.project;

import java.awt.*;
import java.nio.ByteBuffer;

public class Texture
{
    private int width;
    private int height;
    private ByteBuffer data;
    private Image image;
    public int getWidth(){
	return this.width;
    }
    public int getHeight(){
	return this.height;
    }
    public ByteBuffer getData(){
	return this.data;
    }
    public Image getImage(){return this.image;}

    public Texture(int width, int height,Image image, ByteBuffer data){
        this.width = width;
        this.height = height;
        this.data = data;
        this.image = image;
    }
}
