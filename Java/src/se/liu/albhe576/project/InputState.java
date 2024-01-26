package se.liu.albhe576.project;

public class InputState
{
    private boolean k_w;
    private boolean k_a;
    private boolean k_s;
    private boolean k_d;
    private boolean k_space;

    public void setSpace(boolean val){
	this.k_space = val;
    }
    public void setW(boolean val){
	this.k_w = val;
    }
    public void setA(boolean val){
	this.k_a = val;
    }
    public void setS(boolean val){
	this.k_s = val;
    }
    public void setD(boolean val){
	this.k_d = val;
    }

    private int bts(boolean b){
	return b ? 1 : 0;
    }

    @Override public String toString() {
	return String.format("W:%d, A:%d, S:%d, D:%d SB:%d", bts(this.k_w), bts(this.k_a), bts(this.k_s), bts(this.k_d), bts(this.k_space));
    }

    public boolean isWPressed(){
	return this.k_w;
    }
    public boolean isAPressed(){
	return this.k_a;
    }
    public boolean isSPressed(){
	return this.k_s;
    }
    public boolean isDPressed(){
	return this.k_d;
    }
    public boolean isSpacePressed(){
	return this.k_space;
    }
}
