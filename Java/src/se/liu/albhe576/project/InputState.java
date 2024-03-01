package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class InputState
{

    private final byte KEY_W  =(byte)'W';
    private final byte KEY_A  =(byte)'A';
    private final byte KEY_S  =(byte)'S';
    private final byte KEY_D  =(byte)'D';
    private final byte KEY_SPACE  =(byte)32;
    public InputState(long window){
        this.k_mouse_1 = false;
        this.k_mouse_2 = false;
        this.mouseX = 0;
        this.mouseY = 0;
        this.window = window;
        this.initInputHandling();
        Arrays.fill(this.keyboardState, false);
    }
    private final boolean[] keyboardState = new boolean[256];
    private boolean k_mouse_1;
    private boolean k_mouse_2;
    private int mouseX;
    private int mouseY;
    private final long window;

    public void setMouse1(boolean val){
	this.k_mouse_1 = val;
    }
    public void setMouse2(boolean val){
	this.k_mouse_2 = val;
    }

    public void setMousePosition(int mouseX, int mouseY){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    // Java please :(
    private int bts(boolean b){
	return b ? 1 : 0;
    }

    @Override public String toString() {
	return String.format(
		"W:%d, A:%d, S:%d, D:%d SB:%d mouse:(%d,%d), mouse_1:%d, mouse_2:%d",
		bts(this.keyboardState[this.KEY_W]),
		bts(this.keyboardState[this.KEY_A]),
		bts(this.keyboardState[this.KEY_S]),
		bts(this.keyboardState[this.KEY_D]),
		bts(this.keyboardState[this.KEY_SPACE]),
		this.mouseX, this.mouseY,
		bts(this.k_mouse_1),
		bts(this.k_mouse_2)
	);
    }

    public boolean isWPressed(){ return this.keyboardState[this.KEY_W]; }
    public boolean isAPressed(){
        return this.keyboardState[this.KEY_A];
    }
    public boolean isSPressed(){
        return this.keyboardState[this.KEY_S];
    }
    public boolean isDPressed(){
        return this.keyboardState[this.KEY_D];
    }
    public boolean isSpacePressed(){
        return this.keyboardState[this.KEY_SPACE];
    }
    public Point getMousePosition(){return new Point(this.mouseX, this.mouseY);}
    public boolean isMouse1Pressed(){
	return this.k_mouse_1;
    }
    public boolean isMouse2Pressed(){
	return this.k_mouse_2;
    }
    public void initInputHandling(){
        glfwSetMouseButtonCallback(this.window,(window2, button, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_RELEASE){
                boolean mousePressed = action == GLFW_PRESS;
                switch(button){
                    case GLFW_MOUSE_BUTTON_LEFT:{
                        this.setMouse1(mousePressed);
                        break;
                    }
                    case GLFW_MOUSE_BUTTON_RIGHT:{
                        this.setMouse2(mousePressed);
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }
        });

        glfwSetKeyCallback(window, (window2, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                System.out.println("Should close the window");
                glfwSetWindowShouldClose(window, true);
            }
            else if(action == GLFW_PRESS || action == GLFW_RELEASE){
                if(key <= 255){
                    this.keyboardState[key] = action == GLFW_PRESS;
                }else{
                    System.out.printf("Unknown key %d\n", key);
                }
            }
        });
    }
    private void handleMouseInput(){
        DoubleBuffer posBufferX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posBufferY= BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(this.window, posBufferX, posBufferY);

        // This is in range from 0 - ScreenWidth/ScreenHeight
        int posX = -Game.SCREEN_WIDTH + (int) posBufferX.get(0) * 2;
        int posY = -1 * (-Game.SCREEN_HEIGHT + (int) posBufferY.get(0) * 2);

        this.setMousePosition(posX, posY);
    }
}
