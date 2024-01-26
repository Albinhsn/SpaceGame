package se.liu.albhe576.project;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity>
{
    public int compare(Entity e1, Entity e2){
	float z1 = e1.z;
	float z2 = e2.z;
	return Float.compare(z1, z2);
    }
}
