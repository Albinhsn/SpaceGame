package se.liu.albhe576.project;

public class Timer {

    private long lastTick;
    private long serverTicks;
    private boolean running;
    public long getLastTick(){
        return this.lastTick;
    }

    public void reset(){
        this.running = false;
        this.serverTicks = 0;
        this.lastTick = 0;
    }
    public Timer(){
        this.running = false;
        this.serverTicks = 0;
        this.lastTick = 0;
    }
    public void updateTimer(){
        long ticks = System.currentTimeMillis();
        this.lastTick += ticks - this.serverTicks;
        this.serverTicks = ticks;
    }
    public void startTimer(){
        this.running = true;
        this.serverTicks = System.currentTimeMillis();
    }
    public void stopTimer(){
        this.running = false;
    }
}
