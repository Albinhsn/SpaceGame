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
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Byte.toUnsignedInt;

public class GameData
{

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
		    byte val = a > (byte)80 ? (byte)(a * 4) : (byte)0;
		    buffer.put(idx + 0, val);
		    buffer.put(idx + 1, val);
		    buffer.put(idx + 2, val);
		    buffer.put(idx + 3, (byte)0xFF);
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

	return new Texture(image.getWidth(), image.getHeight(), image, buffer);

    }

    private static final String[]FONT_FILE_LOCATIONS = new String[]{
	    "./resources/fonts/font01.png"
    };
    private static final String[]FONT_INFO_LOCATIONS = new String[]{
	    "./resources/fonts/font01.txt"
    };
    private static final String []TEXTURE_LOCATIONS= new String[]{
	"./resources/images/PNG/Sprites/Ships/spaceShips_001.png",
	"./resources/images/PNG/Default/enemy_B.png",
	"./resources/images/PNG/Default/enemy_E.png",
	"./resources/images/PNG/Default/enemy_C.png",
	"./resources/images/PNG/Default/satellite_C.png",
	"./resources/images/PNG/Default/effect_purple.png",
	"./resources/images/PNG/Default/effect_yellow.png"
    };

    public static final float[][] GET_ENTITY_DATA = new float[][]{
	    {0.03f, 0.04f, 0.0f, 0.0f, 0.03f, 0.06f}, // 0
	    {0.03f, 0.05f, 0.0f, 0.0f, 0.03f, 0.06f}, // 1
	    {0.04f, 0.04f, 0.0f, 0.0f, 0.03f, 0.06f}, // 2
	    {0.25f, 0.12f, 0.0f, 0.0f, 0.3f, 0.2f}, // 3
	    {0.05f, 0.1f, -0.03f, 0.0f, 0.05f, 0.1f}, // 4
    };

    public static final float[][] GET_WAVE1_ENEMY_DATA = new float[][]{
	    {0.2f, -1.2f, -0.7f}, // 1
	    {0.6f, 1.2f, -0.6f}, // 2
	    {0.6f, -1.2f, -0.8f}, // 3
	    {1.5f, 1.2f, -0.4f}, // 4
	    {1.5f, 1.2f, -0.2f}, // 5
	    {4.0f, -1.2f, -0.7f}, // 6
	    {4.2f, 1.2f, -0.6f}, // 7
	    {4.2f, -1.2f, -0.8f}, // 8
	    {5.5f, 1.2f, -0.4f}, // 9
	    {5.7f, 1.2f, -0.2f}, // 10
	    {8.0f, -1.2f, -0.7f}, // 11
	    {8.5f, 1.2f, -0.6f}, // 12
	    {9.0f, -1.2f, -0.8f}, // 13
	    {9.2f, 1.2f, -0.4f}, // 14
	    {12.0f, 1.2f, -0.2f}, // 15
	    {12.1f, -1.2f, -0.7f}, // 16
	    {13.0f, 1.2f, -0.6f}, // 17
	    {13.2f, -1.2f, -0.8f}, // 18
	    {13.5f, 1.2f, -0.4f}, // 19
	    {15.0f, 1.2f, -0.2f}, // 20
	    {15.2f, 1.2f, -0.9f}, // 21
	    {15.2f, 1.2f, -0.8f}, // 22
	    {15.2f, 1.2f, -0.7f}, // 13
	    {20.0f, -0.2f, -1.2f}, // 10

    };
    public static String getTextureFileLocation(int index){
	return TEXTURE_LOCATIONS[index];
    }

    public static String getFontFileLocation(int index){return FONT_FILE_LOCATIONS[index];}
    public static String getFontInfoLocation(int index){return FONT_INFO_LOCATIONS[index];}


    private static Enemy createEnemy(int enemyId, Texture texture, int enemyTypeId){
	float [] enemyData =GET_ENTITY_DATA[enemyTypeId];
	Bounds enemy0Bounds = new Bounds(enemyData[0], enemyData[1], enemyData[2], enemyData[3], Color.GRAY, 2,
					 texture.getWidth(), texture.getHeight());

	float[] enemySpawnLocation = GET_WAVE1_ENEMY_DATA[enemyId];
	Enemy enemy = new Enemy(
		(int)(Game.SCREEN_WIDTH * enemySpawnLocation[1]),
		(int)(Game.SCREEN_HEIGHT * enemySpawnLocation[2]),
		enemyData[4],
		enemyData[5],
		texture,
		enemy0Bounds,
		enemySpawnLocation[0],
		enemyTypeId
	);

	return enemy;
    }

    public static ArrayList<Entity> getLevel1() throws IOException {
	// This can still be read from a binary file
	ArrayList<Entity> entities = new ArrayList<>();
	Texture enemyType0Texture = GameData.loadPNGFile(getTextureFileLocation(2));
	Texture enemyType1Texture = GameData.loadPNGFile(getTextureFileLocation(3));
	Texture enemyType2Texture = GameData.loadPNGFile(getTextureFileLocation(4));
	Texture bossTexture = GameData.loadPNGFile(getTextureFileLocation(5));
	Texture[]textures = new Texture[]{
		enemyType0Texture,
		enemyType1Texture,
		enemyType2Texture,
		bossTexture
	};
	int[]waveEnemyTypes = new int[]{
		1,1,2,0,0,
		1,0,0,1,2,
		2,1,1,2,0,
		0,1,1,1,2,
		0,0,0,4
	};
	final int enemies = 24;
	for(int i = 0; i < enemies; i++){
	    entities.add(createEnemy(i, enemyType1Texture, waveEnemyTypes[i]));
	}




	return entities;
    }
}
