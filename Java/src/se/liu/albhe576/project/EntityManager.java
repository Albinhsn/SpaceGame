package se.liu.albhe576.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityManager {
    private         List<EntityData>        entityData;
    private 		List<Wave> 				waves;
    private final AccelerationFunctions     accelerationFunctions;
    private final BulletFactory             bulletFactory;
    private final Logger                    logger          = Logger.getLogger("Entity Manager");
    private final String []WAVE_LOCATIONS= new String[]{
            "./resources/binaryData/wave22024-03-05 23:08:09.345.bin",
            "./resources/binaryData/wave22024-03-06 16:21:02.01.bin"
    };
    public Player getPlayer(){
        EntityData playerData = this.entityData.get(4);
        float width = playerData.width * 100.0f;
        float height = playerData.height * 100.0f;

        return new Player(playerData.hp, 0,0, width, height, playerData.textureIdx);
    }

    public List<Bullet> getBullets(Entity parent){
        return this.bulletFactory.makeBullets(parent);
    }

    private BulletData[] loadBulletData(ResourceManager resourceManager){
        final String bulletDataLocation = "./resources/entities/bulletData.txt";
        try{
            List<String> bulletFileData = Files.readAllLines(Path.of(bulletDataLocation));
            int count = Integer.parseInt(bulletFileData.get(0));


            BulletData [] bulletData = new BulletData[count];

            byte[] binaryData = Files.readAllBytes(Path.of(bulletFileData.get(1)));
            int idx = 0;
            for(int i = 0; i < count; i++){
                BulletData data = new BulletData(
                    ResourceManager.parseIntFromByteArray(binaryData, idx + 0),
                    ResourceManager.parseIntFromByteArray(binaryData, idx + 4),
                    ResourceManager.parseFloatFromByteArray(binaryData, idx + 8),
                    ResourceManager.parseFloatFromByteArray(binaryData, idx + 12),
                    ResourceManager.parseFloatFromByteArray(binaryData, idx + 16)
                );

                idx += BulletData.size;
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

            byte[] binaryData = Files.readAllBytes(Path.of(data.get(1)));
            int idx = 0;

            for(int i = 0; i < count; i++){
                EntityData entityData = new EntityData(
                        ResourceManager.parseIntFromByteArray(binaryData, idx),
                        ResourceManager.parseIntFromByteArray(binaryData, idx + 4),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 8),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 12),
                        ResourceManager.parseIntFromByteArray(binaryData, idx + 16),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 20),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 24),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 28),
                        ResourceManager.parseIntFromByteArray(binaryData, idx + 32),
                        ResourceManager.parseFloatFromByteArray(binaryData, idx + 36)
                );
                idx += EntityData.size;

                this.entityData.add(i, entityData);
            }

        }catch(IOException e){
            logger.severe(String.format("Failed to load entities from '%s'\n", entityDataLocation));
            System.exit(1);
        }
    }
    private void loadWaveData(){
        this.waves = new ArrayList<>();

        for(String waveFileLocation : this.WAVE_LOCATIONS){
            ArrayList<Enemy> enemies = new ArrayList<>();
            final byte[] fileData;

            try{
                fileData = Files.readAllBytes(Path.of(waveFileLocation));
            }catch(IOException e){
                logger.warning(String.format("Failed to load wave from '%s'\n", waveFileLocation));
                continue;
            }

            for(int fileIndex = 0; fileIndex < fileData.length;){
                WaveData enemyWaveData = WaveData.parseFromByteArray(fileData, fileIndex);
                fileIndex += WaveData.size;

                EntityData enemyEntityData = this.entityData.get(enemyWaveData.enemyType);
                AccelerationFunction[] accelerationFunctions = this.accelerationFunctions.getPath(enemyWaveData.pathId);

                float x = 100.0f * enemyWaveData.spawnPositionX;

                float movementSpeed;
                if(x < 100.0f && x > -100.0f){
                    movementSpeed = enemyEntityData.movementSpeed;
                }else{
                    movementSpeed = x < 0 ? enemyEntityData.movementSpeed : -enemyEntityData.movementSpeed;
                }

                Enemy enemy = new Enemy(
                        enemyEntityData.hp,
                        enemyWaveData.enemyType,
                        x,
                        -100.0f * enemyWaveData.spawnPositionY,
                        100.0f * enemyEntityData.width,
                        100.0f * enemyEntityData.height,
                        enemyEntityData.textureIdx,
                        enemyWaveData.spawnTime,
                        enemyEntityData.score,
                        movementSpeed,
                        accelerationFunctions[0],
                        accelerationFunctions[1]

                );
                enemies.add(enemy);
            }
            this.waves.add(new Wave(enemies, -1));
        }
    }

    public Wave getWave(int index, long timeWaveStarted){
        if(index >= this.waves.size()){
            return null;
        }

        ArrayList<Enemy> enemies = new ArrayList<>();

        Wave wave = this.waves.get(index);
        for(Enemy enemy : wave.getEnemies()){
            enemies.add(enemy.copyEnemy());
        }
        return new Wave(enemies, timeWaveStarted);
    }
    public EntityManager(ResourceManager resourceManager){
        this.accelerationFunctions = new AccelerationFunctions();
        this.loadEntityData();
        this.loadWaveData();
        this.bulletFactory = new BulletFactory(this.loadBulletData(resourceManager));
    }
}
