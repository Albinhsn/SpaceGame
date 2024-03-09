package se.liu.albhe576.project;

import java.nio.ByteBuffer;

public class FileBuffer {
    int index;
    byte[] data;

    public int parseIntFromByteBuffer() {
        int out = ByteBuffer.wrap(data, index, 4).getInt();
        index += 4;
        return out;
    }

    public long parseLongFromByteBuffer() {
        long out = ByteBuffer.wrap(data, index, 8).getLong();
        index += 8;
        return out;
    }

    public float parseFloatFromByteBuffer() {
        float out = ByteBuffer.wrap(data, index, 4).getFloat();
        index += 4;
        return out;
    }


    public FileBuffer(byte[] data){
        this.index  = 0;
        this.data   = data;

    }
}
