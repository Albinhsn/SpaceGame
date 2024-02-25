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
    private void handleInput(){

    }

    public void run(){
		Thread platformThread = this.runPlatformLayer();

		while(platformThread.isAlive()){
			this.handleInput();
			}
    }

    public Editor(){
		this.platformLayer = new OpenGLGraphicsLayer(620, 480);
    }
    public static void main(String[] args) {
		Editor editor = new Editor();
		editor.run();
    }
}
