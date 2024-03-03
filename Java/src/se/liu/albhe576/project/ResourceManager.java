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
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ResourceManager
{
	public List<Texture> textures;
	public List<Integer> programs;
	private List<EntityData> entityData;
	private List<Wave> waves;
	static class EntityData{
		public static final int size = 8 * 4;
		int hp;
		int textureIdx;
		float width;
		float height;
		int bulletTextureIdx;
		float bulletSpeed;
		float bulletWidth;
		float bulletHeight;
		public EntityData(int hp, int ti, float w, float h, int bti, float bs, float bw, float bh){
			this.hp = hp;
			this.textureIdx = ti;
			this.width = w;
			this.height = h;
			this.bulletTextureIdx = bti;
			this.bulletSpeed = bs;
			this.bulletWidth = bw;
			this.bulletHeight = bh;
		}
	}

	public void generateTexture(int textureId, int width, int height, ByteBuffer data){

		glBindTexture(GL_TEXTURE_2D, textureId);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

		glGenerateMipmap(GL_TEXTURE_2D);
	}

	private void generateVertexArrayAndVertexBuffer(Texture texture){
		final int []indices = new int[]{0,1,2,1,3,2};
		final float[] bufferData = new float[]{
				-1.0f, -1.0f, 0.0f, 1.0f, //
				1.0f, -1.0f, 1.0f, 1.0f, //
				-1.0f, 1.0f, 0.0f, 0.0f, //
				1.0f, 1.0f, 1.0f, 0.0f, //
		};

		texture.vertexArrayId = glGenVertexArrays();


		glBindVertexArray(texture.vertexArrayId);

		final int vertexBufferId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, bufferData, GL_STATIC_DRAW);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

		final int indexBufferId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

	}

	private void compileTextureShader(){
		final int programId = glCreateProgram();

		int vShader = createAndCompileShader("./shaders/texture.vs", GL_VERTEX_SHADER);
		int pShader = createAndCompileShader("./shaders/texture.ps", GL_FRAGMENT_SHADER);

		glAttachShader(programId, vShader);
		glAttachShader(programId, pShader);

		glBindAttribLocation(programId, 0, "inputPosition");
		glBindAttribLocation(programId, 1, "inputTexCoord");

		glLinkProgram(programId);
		int []status = new int[1];
		glGetProgramiv(programId,GL_LINK_STATUS, status);
		if(status[0] != 1){
			System.out.println("Failed to link program");
			System.exit(1);
		}
		this.programs.add(0, programId);
	}
	private void loadTextures(){
        this.textures = new ArrayList<>();

        for (String textureLocation : this.TEXTURE_LOCATIONS) {
			Texture texture;
			try {
				if(textureLocation.contains("png")){
					texture = this.loadPNGFile(textureLocation);
				}else{
					assert(textureLocation.contains("tga"));
					texture = TGAImage.decodeTGAImageFromFile(textureLocation);
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			texture.textureId = glGenTextures();
			this.generateTexture(texture.textureId, texture.getWidth(), texture.getHeight(), texture.getData());
			this.generateVertexArrayAndVertexBuffer(texture);
			this.textures.add(texture);
		}

		this.compileTextureShader();
	}
	public ResourceManager(){
		this.programs = new ArrayList<>(1);
		this.loadTextures();
		this.loadEntityData();
		this.loadWaveData();
	}


	private int parseIntFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getInt();
	}
	private long parseLongFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 8).getLong();
	}
	private float parseFloatFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getFloat();
	}
	public Wave getWave(int index){
		ArrayList<Enemy> enemies = new ArrayList<>();
		Wave wave = this.waves.get(index);
		for(Enemy enemy : wave.getEnemies()){
			enemies.add(new Enemy(
				enemy.hp,
				enemy.type,
				enemy.x,
				enemy.y,
				enemy.width,
				enemy.height,
				enemy.getTextureIdx(),
				enemy.spawnTime,
				enemy.pathId
			));
		}
		return new Wave(enemies);
	}

	private void loadWaveData(){
		this.waves = new ArrayList<>();

		for(String waveFileLocation : this.WAVE_LOCATIONS){
			ArrayList<Enemy> enemies = new ArrayList<>();
			final byte[] fileData;

			try{
				fileData = Files.readAllBytes(Path.of(waveFileLocation));
			}catch(IOException e){
				e.printStackTrace();
				continue;
			}

			for(int fileIndex = 0; fileIndex < fileData.length;){
				final int enemyType 	   = this.parseIntFromByteArray(fileData, fileIndex + 0);
				final long spawnTime	   = this.parseLongFromByteArray(fileData, fileIndex + 4);
				final float spawnPositionX = this.parseFloatFromByteArray(fileData, fileIndex + 12);
				final float spawnPositionY = this.parseFloatFromByteArray(fileData, fileIndex + 16);
				final int pathId 		   = this.parseIntFromByteArray(fileData, fileIndex + 20);
				fileIndex += 24;

				EntityData enemyEntityData = this.entityData.get(enemyType);
				enemies.add(new Enemy(
						enemyEntityData.hp,
						enemyType,
						Game.SCREEN_WIDTH * spawnPositionX,
						-Game.SCREEN_HEIGHT * spawnPositionY,
						Game.SCREEN_WIDTH * enemyEntityData.width,
						Game.SCREEN_HEIGHT * enemyEntityData.height,
						enemyEntityData.textureIdx,
						spawnTime,
						pathId
				));
			}
			this.waves.add(new Wave(enemies));
		}
	}

	private void loadEntityData(){
		final String entityDataLocation = "./resources/entities/entityData.txt";
		try{
			List<String> data = Files.readAllLines(Path.of(entityDataLocation));
			int count = Integer.parseInt(data.get(0));

			this.entityData = new ArrayList<>(count);

			byte[] binaryData = Files.readAllBytes(Path.of(data.get(1)));
			int idx = 0;

			for(int i = 0; i < count; i++){
				EntityData entityData = new EntityData(
					this.parseIntFromByteArray(binaryData, idx),
					this.parseIntFromByteArray(binaryData, idx + 4),
					this.parseFloatFromByteArray(binaryData, idx + 8),
					this.parseFloatFromByteArray(binaryData, idx + 12),
					this.parseIntFromByteArray(binaryData, idx + 16),
					this.parseFloatFromByteArray(binaryData, idx + 20),
					this.parseFloatFromByteArray(binaryData, idx + 24),
					this.parseFloatFromByteArray(binaryData, idx + 28)
				);
				idx += EntityData.size;

				this.entityData.add(i, entityData);

			}

		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);

		}
	}

	public Player getPlayer(){
		EntityData playerData = this.entityData.get(4);
		float width = playerData.width * Game.SCREEN_WIDTH;
		float height = playerData.height * Game.SCREEN_HEIGHT;
		return new Player(playerData.hp, 0,0, width, height, playerData.textureIdx);
	}

	public Bullet createNewBullet(Entity parent){
		int enemyType = parent instanceof Enemy ? ((Enemy)parent).type : 4;
		int dir = enemyType == 4 ? 1 : -1;

		EntityData data = this.entityData.get(enemyType);
		final float yOffset = (parent.height + data.bulletHeight) * 0.5f;

		return new Bullet(
			parent.x,
			parent.y + yOffset * dir,
			data.bulletWidth * Game.SCREEN_WIDTH,
			data.bulletHeight * Game.SCREEN_HEIGHT,
			data.bulletTextureIdx,
			parent,
		data.bulletSpeed * dir * Game.SCREEN_HEIGHT,
			dir == 1 ? 0.0f : 180.0f
		);
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
	// No reason not to have this in a text file
	// Also make variables for each texture
    private final String []TEXTURE_LOCATIONS= new String[]{
			"./resources/images/PNG/Sprites/Ships/spaceShips_001.png",
			"./resources/images/PNG/Sprites/Missiles/spaceMissiles_012.png",
			"./resources/images/PNG/Default/enemy_B.tga",
			"./resources/images/PNG/Default/enemy_E.tga",
			"./resources/images/PNG/Default/enemy_C.tga",
			"./resources/images/PNG/Default/satellite_C.tga",
			"./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
			"./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
			"./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
			"./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
			"./resources/images/PNG/Default/meteor_detailedLarge.tga",
			"./resources/images/PNG/Default/tile_0044.tga",
			"./resources/UI/grey_button02.png",
			"./resources/UI/grey_box.png",
			"./resources/UI/grey_checkmarkGrey.png",
			"./resources/UI/grey_sliderUp.png",
			"./resources/UI/grey_sliderHorizontal.png",
    };
	// No reason not to have this in a text file
	private final String []WAVE_LOCATIONS= new String[]{
			"./resources/binaryData/wave12024-03-01 10:28:47.53.bin",
	};

	private String getShaderSource(String fileLocation) throws IOException {
		return Files.readString(Paths.get(fileLocation));
	}

	private int createAndCompileShader(String fileLocation, int shaderType){
		String source = null;
		try{
			source = getShaderSource(fileLocation);
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}

		int shader = glCreateShader(shaderType);
		glShaderSource(shader, source);
		glCompileShader(shader);

		IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
		glGetShaderiv(shader, GL_COMPILE_STATUS, intBuffer);
		if(intBuffer.get(0) != 1){
			String log = glGetShaderInfoLog(shader);
			System.out.println("Error loading shader\n");
			System.out.println(log);
			System.exit(2);
		}

		return shader;
	}
}
