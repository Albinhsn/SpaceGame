package se.liu.albhe576.project;

public class Letter
{
	float left, right;
	int size;
	public Letter(float left, float right, int size){
	    this.left = left;
	    this.right = right;
	    this.size = size;
	}

	@Override public String toString() {
	    return "Char{" + "left=" + left + ", right=" + right + ", size=" + size + '}';
	}
}