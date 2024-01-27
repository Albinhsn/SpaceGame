package se.liu.albhe576.project;

public class Editor
{
    private final PlatformLayer platformLayer;


    private Thread runPlatformLayer(){
	Thread platformThread = new Thread(this.platformLayer);
	platformThread.start();
	return platformThread;
    }
    private final List<>
    private long lastInput;
    private void handleInput(){
	InputState inputState = this.platformLayer.getInputState();
	if(inputState.isMouse1Pressed() && lastInput + 20 <= System.currentTimeMillis()){
	    lastInput = System.currentTimeMillis();


	}
    }

    public void run(){
	Thread platformThread = this.runPlatformLayer();
	lastInput = System.currentTimeMillis();

	while(platformThread.isAlive()){
	    this.handleInput();
	    System.out.println(this.platformLayer.inputState);
	}
    }

    public Editor(){
	this.platformLayer = new OpenGLPlatformLayer(620, 480);
    }
    public static void main(String[] args) {
	Editor editor = new Editor();
	editor.run();
    }
}
