package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Change name to resourceManager or something
public class ResourceManager
{
	class EntityData{
		@Override
		public String toString() {
			return String.format("%d %f %f %f %f %f %f", textureId, boundsWidth, boundsHeight, boundsXOffset, boundsYOffset, width, height);
		}

		int textureId;
		float boundsWidth;
		float boundsHeight;
		float boundsXOffset;
		float boundsYOffset;
		float width;
		float height ;
	};
	public ResourceManager(){
		this.loadEntityData();
	}

	private List<EntityData> entityData;

	private int parseIntFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getInt();
	}

	private float parseFloatFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getFloat();
	}
	private void loadEntityData(){
		try{
			List<String> data = Files.readAllLines(Path.of("./resources/entities/entityData.txt"));
			int count = Integer.parseInt(data.get(0));
			System.out.println(data + " " + count);
			this.entityData = new ArrayList<>(count);

			byte[] binaryData = Files.readAllBytes(Path.of(data.get(1)));
			int idx = 0;

			assert(binaryData.length / count == 6 * 4 + 4);

			for(int i = 0; i < count; i++){
				EntityData entityData = new EntityData();

				entityData.textureId 		= this.parseIntFromByteArray(binaryData, idx);
				entityData.boundsWidth  	= this.parseFloatFromByteArray(binaryData, idx + 4);
				entityData.boundsHeight 	= this.parseFloatFromByteArray(binaryData, idx + 8);
				entityData.boundsXOffset	= this.parseFloatFromByteArray(binaryData, idx + 12);
				entityData.boundsYOffset	= this.parseFloatFromByteArray(binaryData, idx + 16);
				entityData.width			= this.parseFloatFromByteArray(binaryData, idx + 20);
				entityData.height 			= this.parseFloatFromByteArray(binaryData, idx + 24);
				idx += 28;

				this.entityData.add(i, entityData);

			}

		}catch(IOException e){
			e.printStackTrace();

		}
	}

	public Player getPlayer(){
		EntityData playerData = this.entityData.get(0);
		float width = playerData.width * Game.SCREEN_WIDTH;
		float height = playerData.height * Game.SCREEN_HEIGHT;
		return new Player(0,0, width, height, 0, 0);
	}
	public Wave loadWave(){

		return null;
	}
	public ArrayList<Entity> loadEntities() throws IOException {
		ArrayList<Entity> entities = new ArrayList<>();
		ArrayList<Entity> waveData = getLevel1();
		//entities.addAll(waveData);

		return entities;
	}

    public Texture loadPNGFile(String fileLocation) throws IOException {
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

		return new Texture(image.getWidth(), image.getHeight(), buffer);

    }

    private final String[]FONT_FILE_LOCATIONS = new String[]{
	    "./resources/fonts/font01.png"
    };
    private final String[]FONT_INFO_LOCATIONS = new String[]{
	    "./resources/fonts/font01.txt"
    };
    public final String []TEXTURE_LOCATIONS= new String[]{
	"./resources/images/PNG/Sprites/Ships/spaceShips_001.png",
	"./resources/images/PNG/Default/enemy_B.png",
	"./resources/images/PNG/Default/enemy_E.png",
	"./resources/images/PNG/Default/enemy_C.png",
	"./resources/images/PNG/Default/satellite_C.png",
	"./resources/images/PNG/Default/effect_purple.png",
	"./resources/images/PNG/Default/effect_yellow.png"
    };

    public final float[][] GET_ENTITY_DATA = new float[][]{
	    {0.03f, 0.04f, 0.0f, 0.0f, 0.03f, 0.06f}, // 0
	    {0.03f, 0.05f, 0.0f, 0.0f, 0.03f, 0.06f}, // 1
	    {0.04f, 0.04f, 0.0f, 0.0f, 0.03f, 0.06f}, // 2
	    {0.25f, 0.12f, 0.0f, 0.0f, 0.3f, 0.2f}, // 3
	    {0.05f, 0.1f, -0.03f, 0.0f, 0.05f, 0.1f}, // 4
    };

    public final float[][] GET_WAVE1_ENEMY_DATA = new float[][]{
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
    public String getTextureFileLocation(int index){
		return TEXTURE_LOCATIONS[index];
    }

    public String getFontFileLocation(int index){return FONT_FILE_LOCATIONS[index];}
    public String getFontInfoLocation(int index){return FONT_INFO_LOCATIONS[index];}


    private Enemy createEnemy(int enemyId, Texture texture, int enemyTypeId){
		float [] enemyData =GET_ENTITY_DATA[enemyTypeId];
		Bounds enemy0Bounds = new Bounds(enemyData[0], enemyData[1], enemyData[2], enemyData[3]);

		float[] enemySpawnLocation = GET_WAVE1_ENEMY_DATA[enemyId];
		//Enemy enemy = new Enemy(
		//	(int)(Game.SCREEN_WIDTH * enemySpawnLocation[1]),
		//	(int)(Game.SCREEN_HEIGHT * enemySpawnLocation[2]),
		//	enemyTypeId
		//);

		//return enemy;
			return null;
    }

    public ArrayList<Entity> getLevel1() throws IOException {
		// This can still be read from a binary file
		ArrayList<Entity> entities = new ArrayList<>();
		Texture enemyType0Texture = loadPNGFile(getTextureFileLocation(2));
		Texture enemyType1Texture = loadPNGFile(getTextureFileLocation(3));
		Texture enemyType2Texture = loadPNGFile(getTextureFileLocation(4));
		Texture bossTexture = loadPNGFile(getTextureFileLocation(5));
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
