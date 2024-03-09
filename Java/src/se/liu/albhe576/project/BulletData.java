package se.liu.albhe576.project;

import java.nio.ByteBuffer;
public class BulletData {
    public static int size = 5 * 4;
    public int textureIdx;
    public int accelerationFunctionIndex;
    public float movementSpeed;
    public float width;
    public float height;
    public byte[] bulletDataToBytes(){
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putInt(0, textureIdx);
        buffer.putInt(4, accelerationFunctionIndex);
        buffer.putFloat(8, movementSpeed);
        buffer.putFloat(12, width);
        buffer.putFloat(16, height);

        byte[] out = new byte[size];
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

