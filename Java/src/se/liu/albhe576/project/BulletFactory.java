package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

public class BulletFactory {

    private final AccelerationFunctions paths = new AccelerationFunctions();
    private final BulletData[] bulletData;

    private static int getDirection(Entity parent){
        int playerEntityIdx = ResourceManager.STATE_VARIABLES.get("playerEntityIdx").intValue();
        int entityIdx = parent instanceof Enemy ? ((Enemy)parent).type : playerEntityIdx;
        return entityIdx == playerEntityIdx ? -1 : 1;
    }
    private int getEntityIdx(Entity parent){
        int playerEntityIdx = ResourceManager.STATE_VARIABLES.get("playerEntityIdx").intValue();
        return parent instanceof Enemy ? ((Enemy)parent).type : playerEntityIdx;
    }

    private Bullet createNewBullet(int textureIdx, float x, float y, float width, float height, float movementSpeed, Entity parent, AccelerationFunction[] paths){
        int dir = BulletFactory.getDirection(parent);
        return new Bullet(
                x,
                y + dir * height,
                width * 100.0f,
                height * 100.0f,
                textureIdx,
                parent,
                dir == -1 ? 0.0f : 180.0f,
                paths[0],
                paths[1],
                dir * movementSpeed * 100.0f
        );
    }

    private final IBulletCreation createSingle = (parent, bulletData) -> {

        ArrayList<Bullet> newBullets = new ArrayList<>();
        newBullets.add(this.createNewBullet(
                bulletData.textureIdx,
                parent.x,
                parent.y,
                bulletData.width,
                bulletData.height,
                bulletData.movementSpeed,
                parent,
                this.paths.getPath(bulletData.accelerationFunctionIndex)
        ));
        return newBullets;
    };

    private final IBulletCreation createDouble= (parent, bulletData) -> {

        ArrayList<Bullet> newBullets = new ArrayList<>();
        float xOffset = parent.width * 0.25f;
        newBullets.add(this.createNewBullet(
                bulletData.textureIdx,
                parent.x + xOffset,
                parent.y,
                bulletData.width,
                bulletData.height,
                bulletData.movementSpeed,
                parent,
                this.paths.getPath(bulletData.accelerationFunctionIndex)
        ));
        newBullets.add(this.createNewBullet(
                bulletData.textureIdx,
                parent.x - xOffset,
                parent.y,
                bulletData.width,
                bulletData.height,
                bulletData.movementSpeed,
                parent,
                this.paths.getPath(bulletData.accelerationFunctionIndex)
        ));
        return newBullets;
    };
    private final IBulletCreation createDoubleOppositeX = (parent, bulletData) -> {

        ArrayList<Bullet> newBullets = new ArrayList<>();
        newBullets.add(this.createNewBullet(
                bulletData.textureIdx,
                parent.x,
                parent.y,
                bulletData.width,
                bulletData.height,
                bulletData.movementSpeed,
                parent,
                this.paths.getPath(bulletData.accelerationFunctionIndex)
        ));
        bulletData.accelerationFunctionIndex++;
            newBullets.add(this.createNewBullet(
                bulletData.textureIdx,
                parent.x,
                parent.y,
                bulletData.width,
                bulletData.height,
                bulletData.movementSpeed,
                parent,
                this.paths.getPath(bulletData.accelerationFunctionIndex)
        ));
        bulletData.accelerationFunctionIndex--;
        return newBullets;
    };
    private final IBulletCreation[] bulletFunctions = new IBulletCreation[]{
            createSingle,
            createSingle,
            createDoubleOppositeX,
            createDouble,
            createSingle,
    };

    public List<Bullet> makeBullets(Entity parent){
        int entityIdx = this.getEntityIdx(parent);


        return this.bulletFunctions[entityIdx].createBullets(parent, this.bulletData[entityIdx]);
    }


    public BulletFactory(BulletData[] bulletData){
        this.bulletData = bulletData;
    }
}
