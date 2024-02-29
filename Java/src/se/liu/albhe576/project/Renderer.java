package se.liu.albhe576.project;

import java.util.List;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

public class Renderer
{
    private List<Integer> textures;
    private List<Integer> programs;
    private long window;
    private List<Entity> entities;
    private int textureId;

    public Renderer(long window) throws IOException {
        this.window = window;

        this.programId = glCreateProgram();
        int vShader = createAndCompileShader("./shaders/texture.vs", GL_VERTEX_SHADER);
        int pShader = createAndCompileShader("./shaders/texture.ps", GL_FRAGMENT_SHADER);

        glAttachShader(this.programId, vShader);
        glAttachShader(this.programId, pShader);

        glBindAttribLocation(this.programId, 0, "inputPosition");
        glBindAttribLocation(this.programId, 1, "inputTexCoord");

        glLinkProgram(this.programId);
        int []status = new int[1];
        glGetProgramiv(this.programId,GL_LINK_STATUS, status);
        if(status[0] != 1){
            System.out.println("Failed to link program");
            System.exit(1);
        }

        Texture texture = GameData.loadPNGFile(GameData.getTextureFileLocation(0));
        glActiveTexture(GL_TEXTURE0);
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getData());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

        float min = -10, max = 10;

        float[] bufferData = new float[]{
            min,min,0.0f,1.0f, //
            max,min,1.0f,1.0f, //
            min,max,0.0f,0.0f, //
            max,max,1.0f,0.0f, //
        };

        int vertexArrayId = glGenVertexArrays();
        glBindVertexArray(vertexArrayId);

        int vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, bufferData, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 2, GL_FLOAT,false, 4* 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT,false, 4 * 4, 2* 4);

        int []indices = new int[]{0,1,2,1,3,2};
        int indexBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public float[] getTransformationMatrix(float x, float y, float rotation){
        float [] rotationM = new float[]{
                (float)Math.cos(rotation), (float)Math.sin(rotation), 0,
                (float)-Math.sin(rotation), (float)Math.cos(rotation), 0,
                0,0,1
        };
        float [] translationM = new float[]{
                1,0,0,
                0,1,0,
                x,y,1
        };
        float [] res = new float[9];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3;j++){
                for(int k = 0; k < 3; k++){
                    res[i + j * 3] += translationM[i + k * 3] * rotationM[k + j * 3];
                }
            }
        }


        return res;
    }

    int programId;

    // ToDo
    //  Find correct program per entity or atleast know what to call for it
    private void renderEntity(Entity entity){
        glUseProgram(this.programId);
        glActiveTexture(GL_TEXTURE0);

        float [] transMatrix = this.getTransformationMatrix(entity.x, entity.y, entity.getRotation());

        int loc = glGetUniformLocation(this.programId, "transMatrix");
        if(loc == -1){
            System.out.println("Failed to get location of transMatrix");
            glfwSetWindowShouldClose(this.window, true);
            System.exit(1);
        }
        glUniformMatrix3fv(loc, true, transMatrix);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
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
