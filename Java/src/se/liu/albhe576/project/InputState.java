package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class InputState
{
    private final boolean[] keyboardStatePressed  = new boolean[256];
    private final boolean[] keyboardStateReleased = new boolean[256];
    private boolean Mouse_1_Pressed;
    private boolean Mouse_2_Pressed;
    private boolean Mouse_1_Released;
    private boolean Mouse_2_Released;
    private int mouseX;
    private int mouseY;
    private int mouseXDelta;
    private final long window;

    public void resetState(){
        this.Mouse_1_Released = false;
        this.Mouse_2_Released = false;
        Arrays.fill(this.keyboardStateReleased, false);
    }
    public InputState(long window){
        this.Mouse_1_Pressed= false;
        this.Mouse_1_Released = false;
        this.Mouse_2_Pressed= false;
        this.Mouse_2_Released = false;
        this.mouseXDelta = 0;
        this.mouseX = 0;
        this.mouseY = 0;
        this.window = window;
        this.initInputHandling();
        Arrays.fill(this.keyboardStatePressed, false);
        Arrays.fill(this.keyboardStateReleased, false);
    }

    public void setMouse_1_Pressed(boolean val){
        this.Mouse_1_Pressed = val;
    }
    public void setMouse_2_Pressed(boolean val){
	this.Mouse_2_Pressed = val;
    }
    public void setMouse_1_Released(boolean val){
        this.Mouse_1_Released= val;
    }
    public void setMouse_2_Released(boolean val){
        this.Mouse_2_Released= val;
    }

    public void setMousePosition(int mouseX, int mouseY){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public boolean isWPressed(){
        byte KEY_W = (byte) 'W';
        return this.keyboardStatePressed[KEY_W]; }
    public boolean isAPressed(){
        byte KEY_A = (byte) 'A';
        return this.keyboardStatePressed[KEY_A];
    }
    public boolean isSPressed(){
        byte KEY_S = (byte) 'S';
        return this.keyboardStatePressed[KEY_S];
    }
    public boolean isDPressed(){
        byte KEY_D = (byte) 'D';
        return this.keyboardStatePressed[KEY_D];
    }
    public boolean isSpacePressed(){
        byte KEY_SPACE = (byte) 32;
        return this.keyboardStatePressed[KEY_SPACE];
    }
    public Point getMousePosition(){return new Point(this.mouseX, this.mouseY);}
    public boolean isMouse1Pressed(){
        return this.Mouse_1_Pressed;
    }
    public boolean isMouse2Pressed(){
	return this.Mouse_2_Pressed;
    }
    public boolean isMouse1Released(){
        return this.Mouse_1_Released;
    }
    public boolean isMouse2Released(){
        return this.Mouse_2_Released;
    }
    public int getMouseXDelta(){
        return this.mouseXDelta;
    }
    public boolean isPPressed(){
        byte KEY_P = (byte) 'P';
        return this.keyboardStatePressed[KEY_P];
    }
    public void initInputHandling(){
        glfwSetMouseButtonCallback(this.window,(window2, button, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_RELEASE){
                switch(button){
                    case GLFW_MOUSE_BUTTON_LEFT:{
                        if(action == GLFW_PRESS){
                            this.setMouse_1_Pressed(true);
                        }else{
                            this.setMouse_1_Released(true);
                            this.setMouse_1_Pressed(false);
                        }
                        break;
                    }
                    case GLFW_MOUSE_BUTTON_RIGHT:{
                        if(action == GLFW_PRESS){
                            this.setMouse_2_Pressed(true);
                        }else{
                            this.setMouse_2_Released(true);
                            this.setMouse_2_Pressed(false);
                        }
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
                    if(action == GLFW_PRESS){
                        this.keyboardStatePressed[key] = true;
                    }else{
                        this.keyboardStateReleased[key] = true;
                        this.keyboardStatePressed[key] = false;
                    }
                }else{
                    System.out.printf("Unknown key %d\n", key);
                }
            }
        });
    }
    public void handleMouseInput(){
        DoubleBuffer posBufferX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posBufferY= BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(this.window, posBufferX, posBufferY);

        int posX = (int) posBufferX.get(0);
        int posY = (int) posBufferY.get(0);

        this.mouseXDelta = this.mouseX - posX;
        this.setMousePosition(posX, posY);
    }
}
