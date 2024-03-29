package se.liu.albhe576.project;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class InputState
{
    private final Logger logger = Logger.getLogger("InputState");
    private final boolean[] keyboardStatePressed  = new boolean[256];
    private final boolean[] keyboardStateReleased = new boolean[256];
    private boolean Mouse_1_Pressed;
    private boolean Mouse_1_Released;
    private boolean backspaceReleased;
    private boolean enterReleased;
    private boolean escapeReleased;
    private int mouseX;
    private int mouseY;
    private final long window;

    public void resetState(){
        this.Mouse_1_Released   = false;
        this.backspaceReleased  = false;
        this.enterReleased  = false;
        this.escapeReleased    = false;
        Arrays.fill(this.keyboardStateReleased, false);
    }
    public InputState(long window){
        this.Mouse_1_Pressed    = false;
        this.Mouse_1_Released   = false;
        this.backspaceReleased  = false;
        this.enterReleased      = false;
        this.escapeReleased    = false;
        this.mouseX             = 0;
        this.mouseY             = 0;
        this.window             = window;
        this.initInputHandling();
        Arrays.fill(this.keyboardStatePressed, false);
    }
    public void setMouse_1_Pressed(boolean val){
        this.Mouse_1_Pressed = val;
    }
    public void setMouse_1_Released(boolean val){
        this.Mouse_1_Released= val;
    }

    public void setMousePosition(int mouseX, int mouseY){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    public boolean[] getKeyboardStateReleased(){
        return this.keyboardStateReleased;
    }
    public boolean isKeyPressed(int key){
        if(key < 256){
            return this.keyboardStatePressed[key];
        }
        return false;
    }
    public boolean isKeyReleased(int key){
        if(key < 256){
            return this.keyboardStateReleased[key];
        }
        if(key == GLFW_KEY_ESCAPE){
            return this.escapeReleased;
        }
        if(key == GLFW_KEY_ENTER){
            return this.enterReleased;
        }
        if(key == GLFW_KEY_BACKSPACE){
            return this.backspaceReleased;
        }

        return false;

    }
    public Point getMousePosition(){return new Point(this.mouseX, this.mouseY);}
    public boolean isMouse1Pressed(){
        return this.Mouse_1_Pressed;
    }
    public boolean isMouse1Released(){
        return this.Mouse_1_Released;
    }
    public void initInputHandling(){
        glfwSetMouseButtonCallback(this.window,(window2, button, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_RELEASE){
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    if (action == GLFW_PRESS) {
                        this.setMouse_1_Pressed(true);
                    } else {
                        this.setMouse_1_Released(true);
                        this.setMouse_1_Pressed(false);
                    }
                }
            }
        });

        glfwSetKeyCallback(window, (window2, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS || action == GLFW_RELEASE){
                if(key <= 255){
                    if(action == GLFW_PRESS){
                        this.keyboardStatePressed[key] = true;
                    }else {
                        this.keyboardStateReleased[key] = true;
                        this.keyboardStatePressed[key] = false;
                    }
                }else if(key == GLFW_KEY_BACKSPACE) {
                    this.backspaceReleased = action == GLFW_RELEASE;
                }else if(key == GLFW_KEY_ENTER){
                    this.enterReleased = action == GLFW_RELEASE;
                }else if(key == GLFW_KEY_ESCAPE){
                    this.escapeReleased = action == GLFW_RELEASE;

                }else{
                    logger.info(String.format("Unknown key %d", key));

                }
            }
        });
    }
    private final DoubleBuffer posBufferX = BufferUtils.createDoubleBuffer(1);
    private final DoubleBuffer posBufferY = BufferUtils.createDoubleBuffer(1);
    public void handleMouseInput(){
        glfwGetCursorPos(this.window, posBufferX, posBufferY);

        int posX = (int) posBufferX.get(0);
        int posY = (int) posBufferY.get(0);

        this.setMousePosition(posX, posY);
    }
}
