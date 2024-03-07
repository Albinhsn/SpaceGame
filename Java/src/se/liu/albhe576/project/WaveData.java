package se.liu.albhe576.project;

public class WaveData {
    static final int size = 24;
    int enemyType;
    long spawnTime;
    float spawnPositionX;
    float spawnPositionY;
    int pathId;
    public static WaveData parseFromByteArray(byte[] data, int fileIndex){
        final int enemyType 	   = ResourceManager.parseIntFromByteArray(data, fileIndex + 0);
        final long spawnTime	   = ResourceManager.parseLongFromByteArray(data, fileIndex + 4);
        final float spawnPositionX = ResourceManager.parseFloatFromByteArray(data, fileIndex + 12);
        final float spawnPositionY = ResourceManager.parseFloatFromByteArray(data, fileIndex + 16);
        final int pathId 		   = ResourceManager.parseIntFromByteArray(data, fileIndex + 20);

        return new WaveData(enemyType, spawnTime, spawnPositionX, spawnPositionY, pathId);
    }
    public WaveData(int t, long st, float spx, float spy, int p){
        this.enemyType = t;
        this.spawnTime      = st;
        this.spawnPositionX = spx;
        this.spawnPositionY = spy;
        this.pathId         = p;
    }
}
