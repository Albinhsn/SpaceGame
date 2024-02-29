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
    private final long window;
    private final int screenWidth;
    private final int screenHeight;
    private final ResourceManager resourceManager;

    public Renderer(long window, int screenWidth, int screenHeight, ResourceManager resourceManager) {
        this.window = window;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resourceManager = resourceManager;
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


    public void renderEntity(Entity entity){
        Texture texture = this.resourceManager.textures.get(entity.getTextureIdx());

        int programId = this.resourceManager.programs.get(0);
        glUseProgram(programId);

        glBindTexture(GL_TEXTURE_2D, texture.textureId);

        glBindVertexArray(texture.vertexArrayId);

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





    public void renderEntities(final List<Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
