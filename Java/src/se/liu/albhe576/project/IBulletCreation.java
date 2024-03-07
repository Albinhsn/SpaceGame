package se.liu.albhe576.project;

import java.util.List;

public interface IBulletCreation {
    List<Bullet> createBullets(Entity parent, BulletData bullets);
}
