package se.liu.albhe576.project;

import java.awt.*;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;


public class Renderer
{
    private final long              window;
    private final ResourceManager   resourceManager;
    private Font font;

    public Renderer(long window, ResourceManager resourceManager) {
        this.window = window;
        this.resourceManager = resourceManager;

        this.font = Font.parseFont(resourceManager, "./resources/Font/font02.tga", "./resources/Font/font01.txt");
    }

    public void renderButton(ButtonUIComponent button){
        this.renderUIComponent(button.textureId, button.x,button.y, button.width, button.height);
        this.renderText(button.text, button.x,button.y, button.spaceSize, button.fontSize, button.textColor, true);
    }
    private void setTextShaderParams(Color color){
        int programId = this.resourceManager.getProgramByIndex(1);
        glUseProgram(programId);
        int location = glGetUniformLocation(programId, "fontTexture");
        if(location == -1){
            System.out.println("Failed to find location of 'fontTexture'");
            System.exit(1);
        }
        glUniform1i(location, 0);

        location = glGetUniformLocation(programId, "pixelColor");
        if(location == -1){
            System.out.println("Failed to find location of 'fontTexture'");
            System.exit(1);
        }
        float[] colorFloat = new float[]{
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f
        };
        glUniform4fv(location, colorFloat);
    }
    public void renderText(String text, float x, float y, float spaceSize, float fontSize, Color color, boolean centered){
        this.font.updateText(text, x, y, spaceSize, fontSize, centered);
        this.setTextShaderParams(color);
        glBindVertexArray(this.font.dynamicVertexArrayId);
        glBindTexture(GL_TEXTURE_2D, this.font.fontTexture.textureId);

        glDrawElements(GL_TRIANGLES, Font.textMaxLength * 4, GL_UNSIGNED_INT, 0);
    }

    public void renderCheckbox(CheckboxUIComponent checkbox){
        this.renderUIComponent(checkbox.textureId, checkbox.x, checkbox.y, checkbox.width, checkbox.height);
        if(checkbox.toggled){
            this.renderUIComponent(checkbox.checkmarkTextureId, checkbox.x, checkbox.y, checkbox.checkmarkWidth, checkbox.checkmarkHeight);
        }
    }
    public void renderUIComponent(int textureId, float x, float y, float width, float height){
        float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
        this.renderTexture(transMatrix, textureId);
    }

    public void renderSlider(SliderUIComponent slider){
        // This one is mostly for debugging/showcase purpose
        renderText(String.format("%d", (int)slider.value), slider.x, (int)(slider.y + slider.height * 1.5), ResourceManager.STATE_VARIABLES.get("fontSpaceSizeSmall"),5f, Color.WHITE, true);

        renderUIComponent(slider.textureId, slider.x, slider.y, slider.width, slider.height);
        final int sliderTextureId = this.resourceManager.textureIdMap.get(Texture.GREY_SLIDER_HORIZONTAL).textureId;
        renderUIComponent(sliderTextureId, slider.x, slider.y, slider.width - 10, slider.height / 10);
        renderUIComponent(slider.sliderTextureId, slider.sliderX, slider.sliderY, slider.sliderWidth, slider.sliderHeight);
    }

    public void renderDropdown(ButtonUIComponent dropdownButton, boolean toggled, List<ButtonUIComponent> dropdownItems){
        this.renderButton(dropdownButton);
        if(toggled){
            for(ButtonUIComponent item :  dropdownItems){
                this.renderButton(item);
            }
        }
    }

    public void renderHealth(int hp){
        final float height    = ResourceManager.STATE_VARIABLES.get("hpHeartHeight");
        final float width     = ResourceManager.STATE_VARIABLES.get("hpHeartWidth");

        final float y         = 100.0f - height;
        float x               = -hp * width / 2;

        Texture texture = this.resourceManager.textureIdMap.get(Texture.HP_HEART);

        for(int i = 0; i < hp; i++, x += width * 1.5f){
            float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
            this.renderTexture(transMatrix, texture.textureId);
        }


    }
    private void renderTexture(float [] transMatrix, int textureId){

        int programId = this.resourceManager.getProgramByIndex(0);
        glUseProgram(programId);
        glBindVertexArray(this.resourceManager.textureVertexArrayId);
        glBindTexture(GL_TEXTURE_2D, textureId);

        int loc = glGetUniformLocation(programId, "transMatrix");
        if(loc == -1){
            System.out.println("Failed to get location of transMatrix");
            glfwSetWindowShouldClose(this.window, true);
            System.exit(1);
        }
        glUniformMatrix3fv(loc, true, transMatrix);

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


    public void renderEntity(Entity entity){
        Texture texture = this.resourceManager.textureIdMap.get(entity.getTextureIdx());
        float [] transMatrix = this.getTransformationMatrix(entity.x, entity.y,entity.width, entity.height, entity.getRotation());
        this.renderTexture(transMatrix, texture.textureId);
    }


    public void renderEntities(final List<? extends Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
