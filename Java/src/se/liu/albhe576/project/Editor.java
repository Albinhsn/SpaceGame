package se.liu.albhe576.project;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Editor
{
    private final OpenGLGraphicsLayer platformLayer;

    private Thread runPlatformLayer(){
	Thread platformThread = new Thread(this.platformLayer);
	platformThread.start();
	return platformThread;
    }
    private long lastInput;
    private final List<ScreenPoint> points;
    private void handleInput(){
	InputState inputState = this.platformLayer.getInputState();
	if(lastInput + 50 <= System.currentTimeMillis()){
	    if(inputState.isMouse1Pressed()){
		lastInput = System.currentTimeMillis();
		Point mousePosition = this.platformLayer.inputState.getMousePosition();
		if(points.stream().noneMatch(p -> p.y == mousePosition.y && p.x == mousePosition.x)){
		    this.points.add(new ScreenPoint(mousePosition.x, mousePosition.y));
		}
	    }

	    if(inputState.isMouse2Pressed()){
		this.points.clear();
	    }
	    this.platformLayer.drawLines(this.points);
	}

    }

    public void run(){
	Thread platformThread = this.runPlatformLayer();

	long lastTick = System.currentTimeMillis();

	while(platformThread.isAlive()){
	    this.handleInput();
	}
    }

    public Editor(){
	this.platformLayer = new OpenGLGraphicsLayer(620, 480, true);
	this.points = new ArrayList<>();
	this.lastInput = System.currentTimeMillis();
    }
    public static void main(String[] args) {
	Editor editor = new Editor();
	editor.run();
    }
}
