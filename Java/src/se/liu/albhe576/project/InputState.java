package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class InputState
{
    public InputState(long window){
        this.k_w = false;
        this.k_a = false;
        this.k_s = false;
        this.k_d = false;
        this.k_space = false;
        this.k_mouse_1 = false;
        this.k_mouse_2 = false;
        this.mouseX = 0;
        this.mouseY = 0;
        this.window = window;
        this.initInputHandling();
    }
    private boolean k_w;
    private boolean k_a;
    private boolean k_s;
    private boolean k_d;
    private boolean k_space;
    private boolean k_mouse_1;
    private boolean k_mouse_2;
    private int mouseX;
    private int mouseY;
    private final long window;

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
		bts(this.k_w),
		bts(this.k_a),
		bts(this.k_s),
		bts(this.k_d),
		bts(this.k_space),
		this.mouseX, this.mouseY,
		bts(this.k_mouse_1),
		bts(this.k_mouse_2)
	);
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
                boolean pressed = action == GLFW_PRESS;
                switch(key){
                    case GLFW_KEY_W:{
                        this.setW(pressed);
                        break;
                    }
                    case GLFW_KEY_A:{
                        this.setA(pressed);
                        break;
                    }
                    case GLFW_KEY_S:{
                        this.setS(pressed);
                        break;
                    }
                    case GLFW_KEY_D:{
                        this.setD(pressed);
                        break;
                    }
                    case GLFW_KEY_SPACE:{
                        this.setSpace(pressed);
                        break;
                    }
                    default:{
                        break;
                    }
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
