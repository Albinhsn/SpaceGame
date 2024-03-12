package se.liu.albhe576.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityManager {
    private         List<EntityData>        entityData;
    private 		List<List<WaveData>> 				waves;
    private final   AccelerationFunctions     accelerationFunctions;
    private final   Logger                    logger          = Logger.getLogger("Entity Manager");
    private final BulletData[] bulletData;
    private int getEntityIdx(Entity parent){
        return parent instanceof Enemy ? ((Enemy)parent).type : ResourceManager.STATE_VARIABLES.getOrDefault("playerEntityIdx", 4.0f).intValue();
    }
    public Player getPlayer(){
        EntityData playerData = this.entityData.get(ResourceManager.STATE_VARIABLES.getOrDefault("playerEntityIdx", 4.0f).intValue());
        return new Player(playerData.hp, 0,0, playerData.width, playerData.height, playerData.textureIdx);
    }

    public Bullet createBullets(Entity parent){
        boolean isPlayer = parent.getClass() == Player.class;
        float yOffset = isPlayer ? 0.5f : -0.5f;
        BulletData data = this.bulletData[this.getEntityIdx(parent)];
        IAccelerationFunction [] accelerationFunctions = this.accelerationFunctions.getPath(data.accelerationFunctionIndex);
        return new Bullet(
            parent.x,
         parent.y + yOffset * parent.height,
            data.width,
            data.height,
            data.textureIdx,
            parent,
            isPlayer ? 0.0f : 180.0f,
            accelerationFunctions[0],
            accelerationFunctions[1],
            isPlayer ? -data.movementSpeed : data.movementSpeed
        );
    }

    private BulletData[] loadBulletData(){
        final String bulletDataLocation = "./resources/entities/bulletData.txt";
        try{
            List<String> bulletFileData = Files.readAllLines(Path.of(bulletDataLocation));
            int count = Integer.parseInt(bulletFileData.get(0));
            BulletData [] bulletData = new BulletData[count];

            FileBuffer bulletDataBuffer = new FileBuffer(Files.readAllBytes(Path.of(bulletFileData.get(1))));
            for(int i = 0; i < count; i++){
                BulletData data = new BulletData(
                    bulletDataBuffer.parseIntFromByteBuffer(),
                    bulletDataBuffer.parseIntFromByteBuffer(),
                    bulletDataBuffer.parseFloatFromByteBuffer(),
                    bulletDataBuffer.parseFloatFromByteBuffer(),
                    bulletDataBuffer.parseFloatFromByteBuffer()
                );
                bulletData[i] = data;
            }

            return bulletData;

        }catch(IOException e){
            e.printStackTrace();
            logger.severe(String.format("Failed to load entities from '%s'\n", bulletDataLocation));
            System.exit(1);
        }
        return null;

    }


    private void loadEntityData(){
        final String entityDataLocation = "./resources/entities/entityData.txt";
        try{
            List<String> data = Files.readAllLines(Path.of(entityDataLocation));
            int count = Integer.parseInt(data.get(0));
            this.entityData = new ArrayList<>(count);

            FileBuffer fileBuffer =new FileBuffer(Files.readAllBytes(Path.of(data.get(1))));

            for(int i = 0; i < count; i++){
                this.entityData.add(i, EntityData.parseFromFileBuffer(fileBuffer));
            }

        }catch(IOException e){
            logger.severe(String.format("Failed to load entities from '%s'\n", entityDataLocation));
            System.exit(1);
        }
    }
    private List<WaveData> parseWaveData(String fileLocation){
        FileBuffer fileBuffer;
        try{
            fileBuffer = new FileBuffer(Files.readAllBytes(Path.of(fileLocation)));
        }catch(IOException e){
            logger.warning(String.format("Failed to read waveData from '%s'", fileLocation));
            return null;
        }
        ArrayList<WaveData> waveData = new ArrayList<>();

        while (fileBuffer.index  < fileBuffer.data.length) {
            waveData.add(WaveData.parseFromFileBuffer(fileBuffer));
        }
        return waveData;

    }
    private void loadWaveData(){
        final String waveFileLocation = "./resources/entities/waveData.txt";
        List<String> data;
        this.waves = new ArrayList<>();
        try{
            data = Files.readAllLines(Path.of(waveFileLocation));
        }catch(IOException e){
            logger.severe(String.format("Failed to read parse waveData from '%s'", waveFileLocation));
            return;
        }

        for(int i = 0; i < Integer.parseInt(data.get(0)); i++){
            List<WaveData> waveData = this.parseWaveData(data.get(i + 1));
            if(waveData != null){
                this.waves.add(waveData);
            }
        }



    }

    public Wave getWave(int index, long timeWaveStarted){
        if(index >= this.waves.size()){
            logger.warning("Trying to access outside of waves array, returning null");
            return null;
        }

        ArrayList<Enemy> enemies = new ArrayList<>();
        for(WaveData enemyWaveData : this.waves.get(index)){

            EntityData enemyEntityData                      = this.entityData.get(enemyWaveData.enemyType);
            IAccelerationFunction[] IAccelerationFunctions = this.accelerationFunctions.getPath(enemyWaveData.pathId);

            float movementSpeed;
            if(enemyWaveData.spawnPositionX < 100.0f && enemyWaveData.spawnPositionX > -100.0f){
                movementSpeed = enemyEntityData.movementSpeed;
            }else{
                movementSpeed = enemyWaveData.spawnPositionX < 0 ? enemyEntityData.movementSpeed : -enemyEntityData.movementSpeed;
            }

            Enemy enemy = new Enemy(
                    enemyEntityData.hp,
                    enemyWaveData.enemyType,
                    enemyWaveData.spawnPositionX,
                    enemyWaveData.spawnPositionY,
                    enemyEntityData.width,
                    enemyEntityData.height,
                    enemyEntityData.textureIdx,
                    enemyWaveData.spawnTime,
                    enemyEntityData.score,
                    movementSpeed,
                    IAccelerationFunctions[0],
                    IAccelerationFunctions[1]

            );
            enemies.add(enemy);
        }

        return new Wave(enemies, timeWaveStarted);
    }
    public EntityManager(){
        this.accelerationFunctions = new AccelerationFunctions();
        this.loadEntityData();
        this.loadWaveData();
        this.bulletData = loadBulletData();
    }
}
