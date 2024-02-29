package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

public class Renderer
{
    private List<Texture> textures;
    private List<Integer> programs;
    private long window;
    private List<Entity> entities;
    private final int screenWidth;
    private final int screenHeight;

    private void loadResources(){
        ResourceManager resourceManager = new ResourceManager();
        String[] textureLocations = resourceManager.TEXTURE_LOCATIONS;
        int numberOfTextures = textureLocations.length;
        this.textures = new ArrayList<>(numberOfTextures);

        int []indices = new int[]{0,1,2,1,3,2};

        for(int i = 0; i < numberOfTextures; i++){
            String textureLocation = textureLocations[i];

            try{
                Texture texture = resourceManager.loadPNGFile(textureLocation);

                glActiveTexture(GL_TEXTURE0 + i);
                texture.textureId = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, texture.textureId);

                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getData());

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

                glGenerateMipmap(GL_TEXTURE_2D);


                float[] bufferData = new float[]{
                        -1.0f,-1.0f,0.0f,1.0f, //
                        1.0f,-1.0f,1.0f,1.0f, //
                        -1.0f,1.0f,0.0f,0.0f, //
                        1.0f,1.0f,1.0f,0.0f, //
                };

                texture.vertexArrayId = glGenVertexArrays();
                glBindVertexArray(texture.vertexArrayId);

                final int vertexBufferId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
                glBufferData(GL_ARRAY_BUFFER, bufferData, GL_STATIC_DRAW);

                glEnableVertexAttribArray(0);
                glEnableVertexAttribArray(1);

                glVertexAttribPointer(0, 2, GL_FLOAT,false, 4* 4, 0);
                glVertexAttribPointer(1, 2, GL_FLOAT,false, 4 * 4, 2* 4);

                final int indexBufferId = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

                this.textures.add(i, texture);
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        this.programs = new ArrayList<>(1);
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

    public Renderer(long window, int screenWidth, int screenHeight) throws IOException {
        this.window = window;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.loadResources();

    }

    private float[] matMul(float [] m0, float[]m1){
        float [] res = new float[9];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3;j++){
                for(int k = 0; k < 3; k++){
                    res[i + j * 3] += m0[i + k * 3] * m1[k + j * 3];
                }
            }
        }
        return res;
    }

    public float[] getTransformationMatrix(float x, float y, float width, float height, float rotation){
        float [] rotationM = new float[]{
                (float)Math.cos(rotation), (float)Math.sin(rotation), 0,
                (float)-Math.sin(rotation), (float)Math.cos(rotation), 0,
                0,0,1
        };


        float transformX = ((x + this.screenWidth) / (float)this.screenWidth) - 1.0f;
        float transformY = ((y + this.screenHeight) / (float)this.screenHeight) - 1.0f;
        float [] translationM = new float[]{
                1,0,0,
                0,1,0,
                transformX, transformY, 1
        };

        float scaleX = ((width + this.screenWidth) / (float)this.screenWidth) - 1.0f;
        float scaleY = ((height + this.screenHeight) / (float)this.screenHeight) - 1.0f;

        float [] scaleM = new float[]{
             scaleX, 0, 0,
             0, scaleY, 0,
             0,  0,1
        };

        float [] m0 = this.matMul(translationM, rotationM);
        return this.matMul(m0, scaleM);
    }


    // ToDo
    //  Find correct program per entity or atleast know what to call for it
    public void renderEntity(Entity entity){
        Texture texture = this.textures.get(entity.getTextureId());

        int programId = this.programs.get(0);
        glUseProgram(programId);
        glBindVertexArray(texture.textureId);

        float [] transMatrix = this.getTransformationMatrix(entity.x, entity.y,entity.width, entity.height, entity.getRotation());

        int loc = glGetUniformLocation(programId, "transMatrix");
        if(loc == -1){
            System.out.println("Failed to get location of transMatrix");
            glfwSetWindowShouldClose(this.window, true);
            System.exit(1);
        }
        glUniformMatrix3fv(loc, true, transMatrix);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }


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



    public void renderEntities(final List<Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
