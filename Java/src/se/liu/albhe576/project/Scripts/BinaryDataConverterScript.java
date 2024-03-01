package se.liu.albhe576.project.Scripts;

import org.lwjgl.BufferUtils;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
           this.spawnPositionX = spx;
           this.spawnPositionY = spy;
           this.pathId         = p;
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
            new WaveData(1, 0.2f, -1.2f, -0.7f, 1),
            new WaveData(1, 0.6f, 1.2f, -0.6f, 1),
            new WaveData(2, 0.6f, -1.2f, -0.8f, 2),
            new WaveData(0, 1.5f, 1.2f, -0.4f, 0),
            new WaveData(0, 1.5f, 1.2f, -0.2f, 0),
            new WaveData(1, 4.0f, -1.2f, -0.7f, 1),
            new WaveData(0, 4.2f, 1.2f, -0.6f, 0),
            new WaveData(0, 4.2f, -1.2f, -0.8f, 0),
            new WaveData(1, 5.5f, 1.2f, -0.4f, 1),
            new WaveData(2, 5.7f, 1.2f, -0.2f, 2),
            new WaveData(2, 8.0f, -1.2f, -0.7f, 2),
            new WaveData(1, 8.5f, 1.2f, -0.6f, 1),
            new WaveData(1, 9.0f, -1.2f, -0.8f, 1),
            new WaveData(2, 9.2f, 1.2f, -0.4f, 2),
            new WaveData(0, 12.0f,1.2f, -0.2f, 0),
            new WaveData(0, 12.1f, -1.2f, -0.7f, 0),
            new WaveData(1, 13.0f,1.2f, -0.6f, 1),
            new WaveData(1, 13.2f, -1.2f, -0.8f, 1),
            new WaveData(1, 13.5f,1.2f, -0.4f, 1),
            new WaveData(2, 15.0f,1.2f, -0.2f, 2),
            new WaveData(0, 15.2f,1.2f, -0.9f, 0),
            new WaveData(0, 15.2f,1.2f, -0.8f, 0),
            new WaveData(2, 15.2f, 1.2f, -0.7f, 0),
            new WaveData(3, 20.0f, -0.2f, -1.2f, 4)
        };
        final int size = 24;
        assert(data.length == size);

        ByteBuffer buffer = ByteBuffer.allocate(WaveData.waveDataSize * size);
        for(int idx = 0; idx < size; idx++){
            buffer.put(WaveData.waveDataSize * idx,  data[idx].waveDataToBytes());
        }
        final int id = 1;

        byte[] out = new byte[WaveData.waveDataSize * size];
        buffer.get(0, out);
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/wave" + id + new Timestamp(System.currentTimeMillis()) + ".bin", out);

    }

    public static void main(String[] args) {
        BinaryDataConverterScript.createWaveData();
    }
}
