package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;


public class Renderer
{
    private final long window;
    private final ResourceManager resourceManager;
    private final Font font;
    private final int fontTextureId;

    public Renderer(long window, ResourceManager resourceManager) {
        this.window = window;
        this.resourceManager = resourceManager;
        this.fontTextureId = glGenTextures();

        this.font = this.loadFont();
    }

    private Font loadFont(){
        try{
            return Font.createFont(Font.TRUETYPE_FONT, new File("./resources/Font/kenvector_future.ttf"));

        }catch (IOException | FontFormatException e){
            e.printStackTrace();
            return null;
        }
    }

    private FontMetrics createFontMetrics(Font font){
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setFont(font);
        return g.getFontMetrics();
    }
    public void renderButton(ButtonUIComponent button){

        renderUIComponent(button.textureId, button.x,button.y, button.width, button.height);
        this.renderTextCentered(button.text, button.x,button.y, button.fontSize, button.textColor);
    }

    public void renderCheckbox(CheckboxUIComponent checkbox){
        renderUIComponent(checkbox.textureId, checkbox.x, checkbox.y, checkbox.width, checkbox.height);
        if(checkbox.toggled){
            renderUIComponent(checkbox.checkmarkTextureId, checkbox.x, checkbox.y, checkbox.checkmarkWidth, checkbox.checkmarkHeight);
        }
    }
    public void renderUIComponent(int textureId, float x, float y, float width, float height){

        float [] transMatrix = this.getTransformationMatrix(
                x,
                y,
                width,
                height,
                0
        );
        glBindTexture(GL_TEXTURE_2D, textureId);
        this.renderTexture(transMatrix);
    }
    public void renderSlider(SliderUIComponent slider){
        renderUIComponent(slider.textureId, slider.x, slider.y, slider.width, slider.height);
        renderUIComponent(17, slider.x, slider.y, slider.width - 10, slider.height / 10);
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
    public void renderTextStartAt(String text, float x, float y, float fontSize, Color color){

        TextImageData data = this.getTextImageData(text, fontSize, color);
        this.resourceManager.generateTexture(this.fontTextureId, data.imageWidth, data.imageHeight, data.buffer);
        float [] transMatrix = this.getTransformationMatrix(x + data.stringWidth, y, data.stringWidth, data.fontHeight, 0);
        this.renderTexture(transMatrix);

    }

    static class TextImageData{
        int stringWidth;
        int fontHeight;
        ByteBuffer buffer;
        int imageWidth;
        int imageHeight;
        public TextImageData(int fontImageWidth, int fontImageHeight, int imageWidth, int imageHeight, ByteBuffer buffer){
            this.stringWidth= fontImageWidth;
            this.fontHeight    = fontImageHeight;
            this.imageWidth         = imageWidth;
            this.imageHeight        = imageHeight;
            this.buffer             = buffer;
        }
    }

    private TextImageData getTextImageData(String text, float fontSize, Color color){
        Font textFont = this.font.deriveFont(fontSize);
        FontMetrics fontMetrics = createFontMetrics(textFont);
        int width = fontMetrics.stringWidth(text);
        int height = fontMetrics.getHeight();

        Font textFont2 = this.font.deriveFont(fontSize * 20);
        FontMetrics fontMetrics2 = createFontMetrics(textFont2);
        int actualWidth =fontMetrics2.stringWidth(text);
        int actualHeight = fontMetrics2.getHeight();


        BufferedImage image = new BufferedImage(actualWidth, actualHeight, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g2d = image.createGraphics();
        g2d.setFont(textFont2);
        g2d.setColor(color);
        g2d.drawString(text, 0, actualHeight - fontMetrics2.getDescent());

        byte[] data = ((DataBufferByte) image.getData().getDataBuffer()).getData();
        for(int i = 0; i < data.length; i+=4){
            byte tmp = data[i + 0];
            data[i  + 0] = data[i + 3];
            data[i + 3] = tmp;

            tmp = data[i + 1];
            data[i + 1] = data[i + 2];
            data[i + 2] = tmp;

        }
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return new TextImageData(width, height, image.getWidth(), image.getHeight(), buffer);

    }

    public void renderTextCentered(String text, float x, float y, float fontSize, Color color){

        TextImageData data   = this.getTextImageData(text, fontSize, color);
        float [] transMatrix = this.getTransformationMatrix(x, y, data.stringWidth, data.fontHeight, 0);
        this.resourceManager.generateTexture(this.fontTextureId, data.imageWidth, data.imageHeight, data.buffer);
        this.renderTexture(transMatrix);
    }

    public void renderHealth(int hp){
        // ToDo hoist this out
        final float height    = 10.0f;
        final float y         = 100.0f - height;
        final float width     = 10.0f;
        float x               = -hp * width / 2;

        Texture texture = this.resourceManager.textureIdMap.get(Texture.HP_HEART);
        glBindTexture(GL_TEXTURE_2D, texture.textureId);
        glBindVertexArray(texture.vertexArrayId);

        for(int i = 0; i < hp; i++, x += width + 2.0f){
            float [] transMatrix = this.getTransformationMatrix(x, y, width, height, 0);
            this.renderTexture(transMatrix);
        }


    }
    private void renderTexture(float [] transMatrix){

        int programId = this.resourceManager.programs.get(0);
        glUseProgram(programId);
        int loc = glGetUniformLocation(programId, "transMatrix");
        if(loc == -1){
            System.out.println("Failed to get location of transMatrix");
            glfwSetWindowShouldClose(this.window, true);
            System.exit(1);
        }
        glUniformMatrix3fv(loc, true, transMatrix);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
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


        float r = (float) (rotation * Math.PI / 180.0f);
        float [] rotationM = new float[]{
                (float)Math.cos(r), (float)Math.sin(r), 0,
                (float)-Math.sin(r), (float)Math.cos(r), 0,
                0,0,1
        };


        float transformX = x / 100.0f;
        float transformY = y / 100.0f;
        float [] translationM = new float[]{
                1,0,0,
                0,1,0,
                transformX, transformY, 1
        };

        float scaleX = width / 100.0f;
        float scaleY = height / 100.0f;

        float [] scaleM = new float[]{
             scaleX, 0, 0,
             0, scaleY, 0,
             0,  0,1
        };

        float [] m0 = this.matMul(translationM, rotationM);
        return this.matMul(m0, scaleM);
    }


    public void renderEntity(Entity entity){
        Texture texture = this.resourceManager.textureIdMap.get(entity.getTextureIdx());

        int programId = this.resourceManager.programs.get(0);
        glUseProgram(programId);
        glBindTexture(GL_TEXTURE_2D, texture.textureId);
        glBindVertexArray(texture.vertexArrayId);

        float [] transMatrix = this.getTransformationMatrix(entity.x, entity.y,entity.width, entity.height, entity.getRotation());

        this.renderTexture(transMatrix);
    }





    public void renderEntities(final List<? extends Entity> entities) {
        for(Entity entity : entities){
            this.renderEntity(entity);
        }
    }

}
