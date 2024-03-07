package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class BulletData {
    @Override
    public String toString() {
        return String.format("%d %d %f %f %f\n", textureIdx, accelerationFunctionIndex, movementSpeed, width, height);
    }

    public static final int size = 5 * 4;
    int textureIdx;
    int accelerationFunctionIndex;
    float movementSpeed;
    float width;
    float height;
    public byte[] bulletDataToBytes(){
        ByteBuffer buffer = ByteBuffer.allocate(BulletData.size);
        buffer.putInt(0, textureIdx);
        buffer.putInt(4, accelerationFunctionIndex);
        buffer.putFloat(8, movementSpeed);
        buffer.putFloat(12, width);
        buffer.putFloat(16, height);

        byte[] out = new byte[BulletData.size];
        buffer.get(out);
        return out;
    }
    public BulletData(int textureIdx, int accelerationFunctionIndex, float movementSpeed, float width, float height){
        this.textureIdx                 = textureIdx;
        this.accelerationFunctionIndex  = accelerationFunctionIndex;
        this.movementSpeed              = movementSpeed;
        this.width                      = width;
        this.height                     = height;

    }
}

