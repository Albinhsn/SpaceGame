package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Game
{
    private final ArrayList<Entity> entities;

    private final PlatformLayer platformLayer;

    public static Texture loadPNGFile(String fileLocation) throws IOException{
        File file = new File(fileLocation);
        BufferedImage image = ImageIO.read(file);

        Raster raster = image.getData();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        byte [] bytes = data.getData();
        ByteBuffer buffer = null;

        ColorModel model = image.getColorModel();
        int pixelSize = model.getPixelSize();
        switch(pixelSize){
            case 8:{
                // ToDo, figure out how to shift this another way
                buffer = BufferUtils.createByteBuffer(bytes.length * 4);
                for(int i = 0, idx = 0; i < bytes.length; i++, idx += 4){
                    byte a = bytes[i];
                    byte val = a > (byte)20 ? (byte)(a * 4) : (byte)0;
                    buffer.put(idx + 0, val);
                    buffer.put(idx + 1, val);
                    buffer.put(idx + 2, val);
                    buffer.put(idx + 3, (byte)0xFF);
                }
                break;
            }
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

        return new Texture(image.getWidth(), image.getHeight(), image, buffer);

    }

    private ArrayList<Entity> loadEntities() throws IOException {
        Texture texture = this.loadPNGFile("./resources/images/PNG/Default/ship_A.png");
        Bounds bounds = new Bounds(0.04f, 0.11f, -0.03f, -0.09f, new Color(255, 255, 255, 255), 8, texture.getWidth(), texture.getHeight());
        Player entity = new Player(0.0f, 0.0f, 0.0f, 0.1f, 0.2f, texture, bounds);
        ArrayList<Entity> entities = new ArrayList<>();
        entities.add(entity);
        return entities;
    }

    public Game() throws IOException {
        this.entities = loadEntities();
        this.platformLayer = new OpenGLPlatformLayer(620, 480);
    }
    private Thread runPlatformLayer(PlatformLayer platformLayer){
        Thread platformThread = new Thread(platformLayer);
        platformThread.start();

        return platformThread;
    }

    private Player getPlayer(){
        return (Player) this.entities.get(0);
    }

    private boolean handleInput(){
        Player player = this.getPlayer();
        InputState inputState = this.platformLayer.getInputState();
        boolean out = false;
        if(inputState.isAPressed()){
            player.moveLeft();
            out = true;
        }
        if(inputState.isDPressed()){
            player.moveRight();
            out = true;
        }
        if(inputState.isWPressed()){
            player.moveUp();
            out = true;
        }
        if(inputState.isSPressed()){
            player.moveDown();
            out = true;
        }
        if(inputState.isSpacePressed()){
            Bullet bullet = player.shoot();
            if(bullet != null){
                this.entities.add(bullet);
            }
            out = true;
        }

        return out;
    }

    private void updateEntities(){
        for(int i = 0; i < entities.size(); i++){
            this.entities.get(i).update();
        }
    }

    private long lastGC;
    private void handleGarbage(){
        final long gcCooldown = 1000;

        if(lastGC + gcCooldown <= System.currentTimeMillis()){
            lastGC = System.currentTimeMillis();
            this.entities.removeIf(s -> !s.isInScene());
        }
    }


    public void runGame(){
        Thread platformThread = this.runPlatformLayer(this.platformLayer);

        this.lastGC = System.currentTimeMillis();


        while(platformThread.isAlive()){
            updateEntities();
            handleInput();
            this.platformLayer.drawEntities(this.entities);

            this.handleGarbage();
        }
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game(); 
        game.runGame();
        System.exit(1);
    }

}
