package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL40.*;

public class Renderer
{
    private final ResourceManager   resourceManager;
    private final Font font;
    private final Logger logger = Logger.getLogger("Renderer");

    public Renderer(ResourceManager resourceManager) {
        this.resourceManager    = resourceManager;
        this.font               = Font.parseFont(resourceManager, "./resources/Font/font01.tga", "./resources/Font/font01.txt");
    }

    public void renderButton(ButtonUIComponent button){
        this.renderUIComponent(button.textureId, button.x,button.y, button.width, button.height);
        this.renderText(button.getText(), button.x,button.y, button.getSpaceSize(), button.getFontSize(), button.getTextColor(), TextLayoutEnum.CENTERED);
    }

    private int getShaderParamLocation(int programId, String name){
        int location = glGetUniformLocation(programId, name);
        if(location == -1){
            logger.severe(String.format("Failed to find location of '%s'", name));
            System.exit(1);
        }
        return location;
    }
    private void setTextShaderParams(Color color){
        int programId = this.resourceManager.getProgramByIndex(1);
        glUseProgram(programId);

        int location  = this.getShaderParamLocation(programId, "fontTexture");
        glUniform1i(location, 0);

        float[] colorFloat = new float[]{
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f
        };
        location  = this.getShaderParamLocation(programId, "pixelColor");
        glUniform4fv(location, colorFloat);
    }

    public void renderText(String text, float x, float y, float spaceSize, float fontSize, Color color, TextLayoutEnum textLayout){
        this.font.updateText(text, x, y, spaceSize, fontSize, textLayout);
        this.setTextShaderParams(color);
        glBindVertexArray(this.font.dynamicVertexArrayId);
        glBindTexture(GL_TEXTURE_2D, this.font.fontTexture.textureId);

        glDrawElements(GL_TRIANGLES, Font.TEXT_MAX_LENGTH * 4, GL_UNSIGNED_INT, 0);
    }

    public void renderCheckbox(CheckboxUIComponent checkbox){
        this.renderUIComponent(checkbox.textureId, checkbox.x, checkbox.y, checkbox.width, checkbox.height);
        if(checkbox.toggled){
            this.renderUIComponent(checkbox.getCheckmarkTextureId(), checkbox.x, checkbox.y, checkbox.getCheckmarkWidth(), checkbox.getCheckmarkHeight());
        }
    }
    public void renderUIComponent(int textureId, float x, float y, float width, float height){
        float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
        this.renderTexture(transMatrix, textureId);
    }

    public void renderDropdown(DropdownUIComponent<?> dropdownUIComponent){
        this.renderButton(dropdownUIComponent.getDropdownButton());
        if(dropdownUIComponent.toggled){
            for(ButtonUIComponent item :  dropdownUIComponent.getDropdownItems()){
                this.renderButton(item);
            }
        }
    }

    public void renderHealth(int hp){
        final float height    = ResourceManager.STATE_VARIABLES.getOrDefault("hpHeartHeight", 10.0f);
        final float width     = ResourceManager.STATE_VARIABLES.getOrDefault("hpHeartWidth", 1.0f);

        // Render in top middle
        final float y         = 100.0f - height;
        float x               = -hp * width / 2;

        Texture texture = this.resourceManager.getTextureById(Texture.HP_HEART);;


        final float gap = 1.5f;
        for(int i = 0; i < hp; i++, x += width * gap){
            float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
            this.renderTexture(transMatrix, texture.textureId);
        }
    }

    private void renderTexture(float [] transMatrix, int textureId){

        int programId = this.resourceManager.getProgramByIndex(0);

        glUseProgram(programId);
        glBindVertexArray(this.resourceManager.textureVertexArrayId);

        glBindTexture(GL_TEXTURE_2D, textureId);

        int location = this.getShaderParamLocation(programId, "transMatrix");
        glUniformMatrix3fv(location, true, transMatrix);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    private float[] matrixMultiplication3x3(float [] m0, float[]m1){
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

    private float[] getTransformationMatrix(float x, float y, float width, float height, float rotation){

        float r = (float) (rotation * Math.PI / 180.0f);
        float [] rotationM = new float[]{
                (float)Math.cos(r), (float)Math.sin(r), 0,
                (float)-Math.sin(r), (float)Math.cos(r), 0,
                0,0,1
        };


        float transformX = x * 0.01f;
        float transformY = y * 0.01f;
        float [] translationM = new float[]{
                1,0,0,
                0,1,0,
                transformX, transformY, 1
        };

        float scaleX = width * 0.01f;
        float scaleY = height * 0.01f;

        float [] scaleM = new float[]{
             scaleX, 0, 0,
             0, scaleY, 0,
             0,  0,1
        };

        float [] m0 = this.matrixMultiplication3x3(translationM, rotationM);
        return this.matrixMultiplication3x3(m0, scaleM);
    }

    private void updateBackgroundData(List<Background.Meteor> meteors){
        final int vertexCount = meteors.size() * 16;
        final float[] bufferData = new float[vertexCount];
        for(int idx = 0, i = 0, j = 0; idx < meteors.size(); idx++, i += 16, j += 6){
            Entity meteor = meteors.get(idx);
            float lowX  = (meteor.x - meteor.width) * 0.01f;
            float highX = (meteor.x + meteor.width) * 0.01f;
            float lowY  = (meteor.y - meteor.height) * 0.01f;
            float highY = (meteor.y + meteor.height) * 0.01f;

            bufferData[i + 0] = lowX;
            bufferData[i + 1] = lowY;
            bufferData[i + 2] = 0.0f;
            bufferData[i + 3] = 1.0f;

            bufferData[i + 4] = highX;
            bufferData[i + 5] = lowY;
            bufferData[i + 6] = 1.0f;
            bufferData[i + 7] = 1.0f;

            bufferData[i + 8] = lowX;
            bufferData[i + 9] = highY;
            bufferData[i + 10] = 0.0f;
            bufferData[i + 11] = 0.0f;

            bufferData[i + 12] = highX;
            bufferData[i + 13] = highY;
            bufferData[i + 14] = 1.0f;
            bufferData[i + 15] = 0.0f;

        }


        glBindVertexArray(this.resourceManager.backgroundVertexArrayId);

        glBindBuffer(GL_ARRAY_BUFFER, this.resourceManager.backgroundBufferId);
        glBufferData(GL_ARRAY_BUFFER, bufferData, GL_DYNAMIC_DRAW);

        int []indices = new int[meteors.size() * 6];
        for(int i = 0, idx = 0; idx < indices.length; idx += 6, i+=4){
            indices[idx + 0] = i + 0;
            indices[idx + 1] = i + 1;
            indices[idx + 2] = i + 2;
            indices[idx + 3] = i + 0;
            indices[idx + 4] = i + 3;
            indices[idx + 5] = i + 1;
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resourceManager.backgroundIndexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }
    public void renderBackground(List<Background.Meteor> meteors){
        this.updateBackgroundData(meteors);
        glUseProgram(this.resourceManager.getProgramByIndex(2));

        Texture texture         = this.resourceManager.getTextureById(meteors.get(0).getTextureIdx());

        int location  = this.getShaderParamLocation(this.resourceManager.getProgramByIndex(2), "shaderTexture");
        glUniform1i(location, 0);
        glBindTexture(GL_TEXTURE_2D, texture.textureId);

        glDrawElements(GL_TRIANGLES, meteors.size() * 4, GL_UNSIGNED_INT, 0);
    }

    public void renderEntity(Entity entity){
        if(ResourceManager.STATE_VARIABLES.getOrDefault("debug", 0.0f).intValue() == 1){
            if(entity.health > 0){
                float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);
                float fontSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeSmall", 3.0f);
                renderText(String.format("%d",entity.health), entity.x, entity.y + entity.height / 2 + fontSize, spaceSize, fontSize, Color.YELLOW, TextLayoutEnum.CENTERED);

            }
        }

        Texture texture         = this.resourceManager.getTextureById(entity.getTextureIdx());
        float [] transMatrix    = this.getTransformationMatrix(entity.x, entity.y,entity.width, entity.height, entity.getRotation());
        this.renderTexture(transMatrix, texture.textureId);
    }


    public void renderEntities(final List<? extends Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
