package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL40.*;

public class ResourceManager
{
	private final 	int[] 					programs 	= new int[2];
	private final   Logger                  logger 		= Logger.getLogger("Resource Manager");
	private Map<Integer, Texture> 	textureIdMap;
	private 		Texture 				tokenTexture;
	public 			int 					textureVertexArrayId;

	public Texture getTextureById(int id){
		return this.textureIdMap.getOrDefault(id, null);
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
			logger.severe(String.format("Failed to link program with id %d", programId));
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
		final String statePath = "./resources/variables/state.txt";
		try{
			stateVariables = Files.readAllLines(Path.of(statePath));
		}catch(IOException e){
			logger.warning(String.format("Unable to load state variables from '%s', will use defaults", statePath));
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
				return Texture.loadPNGFile(textureLocation);
			}else if(textureLocation.contains("tga")){
				return TGAImage.decodeTGAImageFromFile(textureLocation);
			}
		}catch(IOException ignored){
		}
		logger.warning(String.format("Failed to parse texture from '%s'", textureLocation));
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
		this.compileShaders();
	}


	// Also just load this from file?
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
			put(Texture.GREY_BUTTON_05,"./resources/UI/grey_button05.png");
			put(Texture.GREY_BOX, "./resources/UI/grey_box.png");
			put(Texture.GREY_CHECKMARK_GREY, "./resources/UI/grey_checkmarkGrey.png");
			put(Texture.GREY_SLIDER_UP, "./resources/UI/grey_sliderUp.png");
			put(Texture.GREY_SLIDER_HORIZONTAL, "./resources/UI/grey_sliderHorizontal.png");
			put(Texture.GREY_BUTTON_14, "./resources/UI/grey_button14.png");
		}
    };
	public static final Map<String, Float> STATE_VARIABLES = new HashMap<>();
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
			logger.severe(String.format("Error loading shader: \n'%s'", log));
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
