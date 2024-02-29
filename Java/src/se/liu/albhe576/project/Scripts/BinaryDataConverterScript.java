package se.liu.albhe576.project.Scripts;

import org.lwjgl.BufferUtils;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void writeEntityData(){

        final int[] textureIds = new int[]{2,3,4,5,0};
        final int entityDataByteSize = 4 * 7;
        final int totalSize = entityDataByteSize * textureIds.length;

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(totalSize);
        for(int i = 0; i < ResourceManager.GET_ENTITY_DATA.length; i++){
            int textureId = textureIds[i];
            float[] entityData = ResourceManager.GET_ENTITY_DATA[i];


            byte [] idBuffer = ByteBuffer.allocate(4).putInt(textureId).array();
            byte [] boundsWidthBuffer = ByteBuffer.allocate(4).putFloat(entityData[0]).array();
            byte [] boundsHeightBuffer = ByteBuffer.allocate(4).putFloat(entityData[1]).array();
            byte [] boundsXOffsetBuffer = ByteBuffer.allocate(4).putFloat(entityData[2]).array();
            byte [] boundsYOffsetBuffer = ByteBuffer.allocate(4).putFloat(entityData[3]).array();
            byte [] widthBuffer = ByteBuffer.allocate(4).putFloat(entityData[4]).array();
            byte [] heightBuffer = ByteBuffer.allocate(4).putFloat(entityData[5]).array();

            int index = i * entityDataByteSize;
            byteBuffer.put(index + 0 * 4, idBuffer);
            byteBuffer.put(index + 1 * 4, boundsWidthBuffer);
            byteBuffer.put(index + 2 * 4, boundsHeightBuffer);
            byteBuffer.put(index + 3 * 4, boundsXOffsetBuffer);
            byteBuffer.put(index + 4 * 4, boundsYOffsetBuffer);
            byteBuffer.put(index + 5 * 4, widthBuffer);
            byteBuffer.put(index + 6 * 4, heightBuffer);

        }
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/entity" + new Timestamp(System.currentTimeMillis()) + ".bin", bytes);




    }

    private static void writeWaveData(){
        final int id = 0;
        final int waveSize = ResourceManager.GET_WAVE1_ENEMY_DATA.length;
        final int waveEntitySize = 5 * 4;
        final int totalSize = waveEntitySize * waveSize;

        int[]waveEnemyTypes = new int[]{
                1,1,2,0,0,
                1,0,0,1,2,
                2,1,1,2,0,
                0,1,1,1,2,
                0,0,0,4
        };
        int[] pathIds = new int[]{
                1,1,2,0,0,
                1,0,0,1,2,
                2,1,1,2,0,
                0,1,1,1,2,
                0,0,0,4
        };

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(totalSize);
        for(int i = 0; i < ResourceManager.GET_WAVE1_ENEMY_DATA.length; i++){
            int waveEnemyType = waveEnemyTypes[i];
            float[] waveData = ResourceManager.GET_WAVE1_ENEMY_DATA[i];


            byte [] enemyType = ByteBuffer.allocate(4).putInt(waveEnemyType).array();
            byte [] spawnTime = ByteBuffer.allocate(4).putFloat(waveData[0]).array();
            byte [] spawnPositionX= ByteBuffer.allocate(4).putFloat(waveData[1]).array();
            byte [] spawnPositionY= ByteBuffer.allocate(4).putFloat(waveData[2]).array();
            byte [] pathId = ByteBuffer.allocate(4).putFloat(pathIds[i]).array();

            int index = i *  waveEntitySize;
            byteBuffer.put(index + 0, enemyType);
            byteBuffer.put(index + 1 * 4, spawnTime);
            byteBuffer.put(index + 2 * 4, spawnPositionX);
            byteBuffer.put(index + 3 * 4, spawnPositionY);
            byteBuffer.put(index + 4 * 4, pathId);

        }
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        BinaryDataConverterScript.writeBytesToFile("./resources/binaryData/wave" + id + new Timestamp(System.currentTimeMillis()) + ".bin", bytes);

    }



    public static void main(String[] args) {
    }
}
