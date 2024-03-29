package se.liu.albhe576.project.Scripts;

import org.lwjgl.BufferUtils;
import se.liu.albhe576.project.BulletData;
import se.liu.albhe576.project.ResourceManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class BinaryDataConverterScript
{
    private static void writeBytesToFile(String path, byte[] bytes){
        try(FileOutputStream stream = new FileOutputStream(path)){
            stream.write(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) { throw new RuntimeException(e);
        }
    }

    static class WaveData{
       static final int waveDataSize = 24;
       int enemyType;
       long spawnTime;
       float spawnPositionX;
       float spawnPositionY;
       int pathId;
       public WaveData(int t, float st, float spx, float spy, int p){
           this.enemyType = t;
           this.spawnTime      = (long)(1000 *st);
           this.spawnPositionX = spx * 100.0f;
           this.spawnPositionY = (spy == 1.4f ? 1.4f : 1.2f) * 100.0f;
           this.pathId         = p == 3 ? 3 : p - 4;
       }
       byte[] waveDataToBytes(){
           ByteBuffer buffer = ByteBuffer.allocate(WaveData.waveDataSize);
           buffer.putInt(0, this.enemyType);
           buffer.putLong(4,this.spawnTime);
           buffer.putFloat(12, this.spawnPositionX);
           buffer.putFloat(16, this.spawnPositionY);
           buffer.putInt(20, this.pathId);
           byte[] out = new byte[24];
           buffer.get(0, out);

           return out;
       }
    }

    public static void createWaveData(){
        WaveData[] data = new WaveData[]{
            new WaveData(1, 0.2f, 0.7f, 0.7f, 5),
            new WaveData(1, 0.6f, -0.6f, -0.6f, 5),
            new WaveData(3, 0.6f, -0.1f, 1.4f, 3),
            new WaveData(3, 1.5f, 0.1f, 1.4f, 3),
            new WaveData(2, 1.5f,  -0.5f,-0.5f, 6),
            new WaveData(2, 4.0f, 0.5f,0.5f,  6),
            new WaveData(1, 4.2f,-0.0f, -0.0f,  5),
            new WaveData(0, 4.2f, 0.2f,0.2f, 4),
            new WaveData(1, 5.5f, -0.4f,-0.4f,  5),
            new WaveData(3, 5.7f, 0.0f,1.4f,  3),
            new WaveData(2, 8.0f, 0.3f,0.3f,  6),
            new WaveData(1, 8.5f, -0.4f,-0.4f,  5),
            new WaveData(1, 9.0f, -0.1f,0.1f,  5),
            new WaveData(3, 9.2f, 0.3f,1.4f,  3),
            new WaveData(3, 12.0f,-0.3f,1.4f,  3),
            new WaveData(2, 12.1f, 0.2f,0.2f,  6),
            new WaveData(0, 13.0f,-0.4f,-0.4f,  4),
            new WaveData(1, 13.2f, 0.5f,0.5f,  5),
            new WaveData(3, 13.5f,-0.4f,1.4f,  3),
            new WaveData(2, 15.0f,-0.6f,-0.6f,  6),
            new WaveData(1, 15.2f,-0.2f,-0.2f,  5),
            new WaveData(0, 15.2f,-0.4f,-0.4f,  4),
            new WaveData(3, 15.2f,0.2f, 1.4f,  3),
            new WaveData(3, 20.0f, -0.2f, 1.4f, 3)
        };
        final int size = 24;
        assert(data.length == size);

        ByteBuffer buffer = ByteBuffer.allocate(WaveData.waveDataSize * size);
        for(int idx = 0; idx < size; idx++){
            buffer.put(WaveData.waveDataSize * idx,  data[idx].waveDataToBytes());
        }
        final int id = 2;

        byte[] out = new byte[WaveData.waveDataSize * size];
        buffer.get(0, out);
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/wave" + id + new Timestamp(System.currentTimeMillis()) + ".bin", out);

    }

    static class EntityData{
        @Override
        public String toString() {
            return String.format(
                    "%d %d %f %f %d %f %f %f %d %f",
                    hp,
                    textureIdx,
                    width,
                    height,
                    bulletTextureIdx,
                    bulletSpeed,
                    bulletWidth,
                    bulletHeight,
                    score,
                    movementSpeed
            );
        }
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
        public EntityData(int hp, int ti, float w, float h, int score, float ms){
            this.hp = hp;
            this.textureIdx = ti;
            this.width = w;
            this.height = h;
            this.score = score;
            this.movementSpeed = ms;
        }
        byte[] entityDataToBytes(){
            ByteBuffer buffer = ByteBuffer.allocate(EntityData.size);
            buffer.putInt(0, this.hp);
            buffer.putInt(4, this.textureIdx);
            buffer.putFloat(8,this.width);
            buffer.putFloat(12, this.height);
            buffer.putInt(16, this.bulletTextureIdx);
            buffer.putFloat(20, this.bulletSpeed);
            buffer.putFloat(24, this.bulletWidth);
            buffer.putFloat(28, this.bulletHeight);
            buffer.putInt(32, this.score);
            buffer.putFloat(36, this.movementSpeed);

            byte[] out = new byte[EntityData.size];
            buffer.get(0, out);

            return out;
        }
    }
    public static void createEntityData(){
       EntityData[] data = new EntityData[]{
               new EntityData(1,2, 3.0f, 6.0f, 100, 0.2f),
               new EntityData(1,3, 3.0f, 6.0f, 125, 0.3f),
               new EntityData(1,4, 3.0f, 6.0f, 150, 0.1f),
               new EntityData(10, 5, 30.0f, 20.0f,500, 0.4f),
               new EntityData(3,0, 5.0f, 10.0f,0, 1.0f),
       };
       final int size = 5;
        ByteBuffer buffer = ByteBuffer.allocate(EntityData.size * size);
        for(int idx = 0; idx < size; idx++){
            buffer.put(EntityData.size * idx,  data[idx].entityDataToBytes());
        }
        final int id = 4;

        byte[] out = new byte[EntityData.size * size];
        buffer.get(0, out);
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/entity" + id  + "-" + new Timestamp(System.currentTimeMillis()) + ".bin", out);
    }

    public static void createBulletData(){
        BulletData[] data = new BulletData[]{
                new BulletData(6,7,  0.6f, 1.3f, 3.3f),
                new BulletData(7,7,  2.0f, 1.1f,   4.2f),
                new BulletData(8,8,  1.0f, 1.1f, 4.4f),
                new BulletData(9, 7, 3.0f, 4.8f, 6.25f),
                new BulletData(1,7,  1.0f, 1.6f, 2.0f),
        };

        final int size = 5;

        ByteBuffer buffer = ByteBuffer.allocate(BulletData.size * size);
        for(int idx = 0; idx < size; idx++){
            buffer.put(BulletData.size * idx,  data[idx].bulletDataToBytes());
        }
        final int id = 2;

        byte[] out = new byte[BulletData.size * size];
        buffer.get(0, out);
        buffer.flip();
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/bullet" + id  + "-" + new Timestamp(System.currentTimeMillis()) + ".bin", out);
    }

    public static void main(String[] args) {
        //BinaryDataConverterScript.createBulletData();
        //BinaryDataConverterScript.createEntityData();
        BinaryDataConverterScript.createWaveData();;
    }
}
