package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;
import se.liu.albhe576.project.Scripts.BinaryDataConverterScript;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL40.*;

public class ResourceManager
{
	private final 	int[] 			programs = new int[2];
	public 			Map<Integer, Texture> 	textureIdMap;
	private 		List<EntityData> 		entityData;
	private 		List<Wave> 				waves;
	private 		Texture 				tokenTexture;
	public 			int 					textureVertexArrayId;
	static class WaveData{
		static final int size = 24;
		int enemyType;
		long spawnTime;
		float spawnPositionX;
		float spawnPositionY;
		int pathId;
		public static WaveData parseFromByteArray(byte[] data, int fileIndex){
			final int enemyType 	   = ResourceManager.parseIntFromByteArray(data, fileIndex + 0);
			final long spawnTime	   = ResourceManager.parseLongFromByteArray(data, fileIndex + 4);
			final float spawnPositionX = ResourceManager.parseFloatFromByteArray(data, fileIndex + 12);
			final float spawnPositionY = ResourceManager.parseFloatFromByteArray(data, fileIndex + 16);
			final int pathId 		   = ResourceManager.parseIntFromByteArray(data, fileIndex + 20);

			return new WaveData(enemyType, spawnTime, spawnPositionX, spawnPositionY, pathId);
		}
		public WaveData(int t, long st, float spx, float spy, int p){
			this.enemyType = t;
			this.spawnTime      = st;
			this.spawnPositionX = spx;
			this.spawnPositionY = spy;
			this.pathId         = p;
		}
	}
	static class EntityData{
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

	public void generateTexture(int textureId, int width, int height, ByteBuffer data){
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

		glGenerateMipmap(GL_TEXTURE_2D);
	}

	private void generateTextureVertexArray(){
		final int []indices = new int[]{0,1,2,1,3,2};
		final float[] bufferData = new float[]{
				-1.0f, -1.0f, 0.0f, 1.0f, //
				1.0f, -1.0f, 1.0f, 1.0f, //
				-1.0f, 1.0f, 0.0f, 0.0f, //
				1.0f, 1.0f, 1.0f, 0.0f, //
		};

		this.textureVertexArrayId = glGenVertexArrays();
		glBindVertexArray(this.textureVertexArrayId);

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
		glBindVertexArray(0);
	}

	private void compileAndAttachShaders(int programId, String vertexShader, String fragmentShader){
		int vShader = createAndCompileShader(vertexShader, GL_VERTEX_SHADER);
		int pShader = createAndCompileShader(fragmentShader, GL_FRAGMENT_SHADER);

		glAttachShader(programId, vShader);
		glAttachShader(programId, pShader);

	}

	private void linkProgram(int programId){
		glLinkProgram(programId);
		int []status = new int[1];
		glGetProgramiv(programId,GL_LINK_STATUS, status);
		if(status[0] != 1){
			System.out.println("Failed to link program");
			System.exit(1);
		}
	}


	private void compileTextShader(){
		final int programId = glCreateProgram();
		this.programs[1] = programId;

		this.compileAndAttachShaders(programId, "./shaders/font.vs", "./shaders/font.ps");

		glBindAttribLocation(programId, 0, "inputPosition");
		glBindAttribLocation(programId, 1, "inputTexCoord");

		this.linkProgram(programId);
	}

	private void compileTextureShader(){
		final int programId = glCreateProgram();
		this.programs[0] = programId;

		this.compileAndAttachShaders(programId, "./shaders/texture.vs","./shaders/texture.ps" );

		glBindAttribLocation(programId, 0, "inputPosition");
		glBindAttribLocation(programId, 1, "inputTexCoord");
		this.linkProgram(programId);
	}
	private void loadStateVariables() {
		List<String> stateVariables;
		try{
			stateVariables = Files.readAllLines(Path.of("./resources/variables/state.txt"));
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Unable to load state variables, will use defaults");
			return;
		}

		for(String variable : stateVariables){
			String[] keyValuePair = variable.strip().split(" ");
			ResourceManager.STATE_VARIABLES.put(keyValuePair[0], Float.parseFloat(keyValuePair[1]));
		}
	}

	private Texture parseTexture(String textureLocation){
		try{
			if(textureLocation.contains("png")){
				return ResourceManager.loadPNGFile(textureLocation);
			}else if(textureLocation.contains("tga")){
				return TGAImage.decodeTGAImageFromFile(textureLocation);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.printf("WARNING: Unable to parse resource '%s', using token texture", textureLocation);
		return this.tokenTexture;
	}
	public Texture createTexture(String textureLocation){
		Texture texture = this.parseTexture(textureLocation);
		texture.textureId = glGenTextures();
		this.generateTexture(texture.textureId, texture.getWidth(), texture.getHeight(), texture.getData());

		return texture;
	}
	private void loadTextures(){
		this.generateTextureVertexArray();
		this.textureIdMap = new HashMap<>();

		// Create a red rectangle texture just in case some texture is missing
		this.tokenTexture = Texture.createTokenTexture();

        for (Map.Entry<Integer, String> entry : this.TEXTURE_LOCATIONS.entrySet()) {
			Integer key = entry.getKey();
			Texture texture = this.createTexture(entry.getValue());

			this.textureIdMap.put(key, texture);
		}

	}
	private void compileShaders(){
		this.compileTextureShader();
		this.compileTextShader();
	}

	public void loadResources(){
		this.loadTextures();
		this.loadEntityData();
		this.loadWaveData();
		this.compileShaders();
	}


	private static int parseIntFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getInt();
	}
	private static long parseLongFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 8).getLong();
	}
	private static float parseFloatFromByteArray(byte [] data, int idx){
		return ByteBuffer.wrap(data, idx, 4).getFloat();
	}
	public Wave getWave(int index){
		ArrayList<Enemy> enemies = new ArrayList<>();
		Wave wave = this.waves.get(index);
		for(Enemy enemy : wave.enemies()){
			enemies.add(new Enemy(enemy.hp, enemy.type, enemy.x, enemy.y, enemy.width, enemy.height, enemy.getTextureIdx(), enemy.spawnTime, enemy.pathId, enemy.scoreGiven, enemy.moveSpeed));
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
				System.out.printf("Failed to load wave from '%s'\n", waveFileLocation);
				e.printStackTrace();
				continue;
			}

			for(int fileIndex = 0; fileIndex < fileData.length;){
				WaveData enemyWaveData = WaveData.parseFromByteArray(fileData, fileIndex);
				fileIndex += WaveData.size;

				EntityData enemyEntityData = this.entityData.get(enemyWaveData.enemyType);
				enemies.add(new Enemy(
						enemyEntityData.hp,
						enemyWaveData.enemyType,
						100.0f * enemyWaveData.spawnPositionX,
						-100.0f * enemyWaveData.spawnPositionY,
						100.0f * enemyEntityData.width,
						100.0f * enemyEntityData.height,
						enemyEntityData.textureIdx,
						enemyWaveData.spawnTime,
						enemyWaveData.pathId,
						enemyEntityData.score,
						-enemyEntityData.movementSpeed
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
					ResourceManager.parseIntFromByteArray(binaryData, idx),
					ResourceManager.parseIntFromByteArray(binaryData, idx + 4),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 8),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 12),
					ResourceManager.parseIntFromByteArray(binaryData, idx + 16),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 20),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 24),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 28),
					ResourceManager.parseIntFromByteArray(binaryData, idx + 32),
					ResourceManager.parseFloatFromByteArray(binaryData, idx + 36)
				);
				idx += EntityData.size;

				this.entityData.add(i, entityData);

			}

		}catch(IOException e){
			System.out.printf("Failed to load entities from '%s'\n", entityDataLocation);
			System.exit(1);

		}
	}

	public Player getPlayer(){
		EntityData playerData = this.entityData.get(4);
		float width = playerData.width * 100.0f;
		float height = playerData.height * 100.0f;

		return new Player(playerData.hp, 0,0, width, height, playerData.textureIdx);
	}

	public Bullet createNewBullet(Entity parent){
		int playerEntityIdx = ResourceManager.STATE_VARIABLES.get("playerEntityIdx").intValue();
		int entityIdx = parent instanceof Enemy ? ((Enemy)parent).type : playerEntityIdx;
		int dir = entityIdx == playerEntityIdx ? 1 : -1;

		EntityData data = this.entityData.get(entityIdx);
		final float yOffset = (parent.height + data.bulletHeight);

		return new Bullet(
			parent.x,
			parent.y + yOffset * dir,
			data.bulletWidth * 100.0f,
			data.bulletHeight * 100.0f,
			data.bulletTextureIdx,
			parent,
		data.bulletSpeed * dir * 100.0f,
			dir == 1 ? 0.0f : 180.0f
		);
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
	// No reason not to have this in a text file
	// Also make variables for each texture
    private final Map<Integer, String> TEXTURE_LOCATIONS= new HashMap<>()
	{
		{
			put(Texture.PLAYER_MODEL,"./resources/images/PNG/Sprites/Ships/spaceShips_001.png");
			put(Texture.PLAYER_BULLET,"./resources/images/PNG/Sprites/Missiles/spaceMissiles_012.png");
			put(Texture.ENEMY_MODEL_1, "./resources/images/PNG/Default/enemy_B.tga");
			put(Texture.ENEMY_MODEL_2,"./resources/images/PNG/Default/enemy_E.tga");
			put(Texture.ENEMY_MODEL_3, "./resources/images/PNG/Default/enemy_C.tga");
			put(Texture.BOSS_MODEL_1, "./resources/images/PNG/Default/satellite_C.tga");
			put(Texture.ENEMY_BULLET_1, "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png");
			put(Texture.ENEMY_BULLET_2, "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png");
			put(Texture.ENEMY_BULLET_3, "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png");
			put(Texture.ENEMY_BULLET_4, "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png");
			put(Texture.BACKGROUND_METEOR, "./resources/images/PNG/Default/meteor_detailedLarge.tga");
			put(Texture.HP_HEART, "./resources/images/PNG/Default/tile_0044.tga");
			put(Texture.GREY_BUTTON_02,"./resources/UI/grey_button02.png");
			put(Texture.GREY_BOX, "./resources/UI/grey_box.png");
			put(Texture.GREY_CHECKMARK_GREY, "./resources/UI/grey_checkmarkGrey.png");
			put(Texture.GREY_SLIDER_UP, "./resources/UI/grey_sliderUp.png");
			put(Texture.GREY_SLIDER_HORIZONTAL, "./resources/UI/grey_sliderHorizontal.png");
		}
    };
	public static final Map<String, Float> STATE_VARIABLES = new HashMap<>()
	{
		{
			put("playerEntityIdx", 4.0f);
			put("vsync", 1.0f);
			put("SCREEN_WIDTH", 620.0f);
			put("SCREEN_HEIGHT", 480.0f);
			put("waveIdx", 0.0f);
			put("scorePerEnemy", 100.0f);
			put("updateTimerMS", 16.0f);
			put("enemyMS", 0.2f);
			put("enemyGCDMin", 400.0f);
			put("enemyGCDMax", 1000.0f);
			put("playerMS", 1.0f);
			put("playerGCDMS", 500.0f);
			put("buttonSizeSmallWidth", 18.0f);
			put("buttonSizeSmallHeight", 6.0f);
			put("buttonSizeMediumWidth", 32.0f);
			put("buttonSizeMediumHeight", 10.0f);
			put("buttonSizeLargeWidth", 40.0f);
			put("buttonSizeLargeHeight", 10.0f);
			put("fontSizeSmall", 2.0f);
			put("fontSizeMedium", 4.0f);
			put("fontSizeLarge", 8.0f);
			put("checkboxWidth", 6.0f);
			put("checkboxHeight", 8.0f);
			put("buttonTextureIdMapKey", 13.0f);
			put("hpHeartWidth", 10.0f);
			put("hpHeartHeight", 10.0f);
		}
	};
	// No reason not to have this in a text file
	private final String []WAVE_LOCATIONS= new String[]{
			"./resources/binaryData/wave22024-03-05 23:08:09.345.bin",
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
	public int getProgramByIndex(int index){
		return this.programs[index];
	}
	public ResourceManager(){
		this.loadStateVariables();
	}
}
