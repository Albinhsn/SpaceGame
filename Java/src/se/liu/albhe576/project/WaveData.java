package se.liu.albhe576.project;

public class WaveData {
    static final int size = 24;
    int enemyType;
    long spawnTime;
    float spawnPositionX;
    float spawnPositionY;
    int pathId;
    public static WaveData parseFromFileBuffer(FileBuffer fileBuffer){
        return new WaveData(
                fileBuffer.parseIntFromByteBuffer(),
                fileBuffer.parseLongFromByteBuffer(),
                fileBuffer.parseFloatFromByteBuffer(),
                fileBuffer.parseFloatFromByteBuffer(),
                fileBuffer.parseIntFromByteBuffer()
        );
    }
    public WaveData(int t, long st, float spx, float spy, int p){
        this.enemyType = t;
        this.spawnTime      = st;
        this.spawnPositionX = spx;
        this.spawnPositionY = spy;
        this.pathId         = p;
    }
}
