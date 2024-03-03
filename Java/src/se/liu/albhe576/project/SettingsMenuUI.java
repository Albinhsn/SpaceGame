package se.liu.albhe576.project;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public class SettingsMenuUI extends UI {
    private UIState parentState;
    private final ButtonUI returnButton;
    private final CheckboxUI vsyncCheckbox;
    private final DropdownUI<Point> screenSizeDropdown;

    private final SliderUI audioSlider;

    public void setParentState(UIState uiState){
        this.parentState = uiState;
    }

    public UIState render(InputState inputState, Renderer renderer, long window){

        renderer.renderButton(returnButton);
        if(returnButton.isReleased(inputState)){
            System.out.println("Return");
            return this.parentState;
        }

        renderer.renderText("Vsync", -120, 0, 20, Color.WHITE);
        renderer.renderCheckbox(this.vsyncCheckbox);
        if(vsyncCheckbox.isReleased(inputState)){
            vsyncCheckbox.toggled = !vsyncCheckbox.toggled;
            glfwSwapInterval(vsyncCheckbox.toggled ? 1 : 0);
            System.out.println(vsyncCheckbox.toggled);
        }

        renderer.renderDropdown(screenSizeDropdown.dropdownButton, screenSizeDropdown.toggled, screenSizeDropdown.dropdownItems);
        if(this.screenSizeDropdown.dropdownButton.isReleased(inputState)){
            this.screenSizeDropdown.toggled = !this.screenSizeDropdown.toggled;
        }
        if(this.screenSizeDropdown.toggled){
            for(int i = 0; i < this.screenSizeDropdown.dropdownData.length; i++){
                ButtonUI item = this.screenSizeDropdown.dropdownItems.get(i);
                if(item.isReleased(inputState)){
                    this.screenSizeDropdown.toggled = !this.screenSizeDropdown.toggled;
                    Point newWindowSize = this.screenSizeDropdown.dropdownData[i];
                    glfwSetWindowSize(window, newWindowSize.x, newWindowSize.y);
                    Game.SCREEN_WIDTH = newWindowSize.x;
                    Game.SCREEN_HEIGHT = newWindowSize.y;
                    break;
                }
            }
        }

        renderer.renderSlider(this.audioSlider);
        if(this.audioSlider.isPressed(inputState)){
            this.audioSlider.updateSliderPosition(inputState.getMousePosition().x);
        }


        return UIState.SETTINGS_MENU;
    }
    public SettingsMenuUI(){
        this.returnButton       = new ButtonUI(0.0f, -200.0f, 200.0f, 50.0f, "Return", 13, 20.0f, Color.ORANGE);
        this.vsyncCheckbox      = new CheckboxUI(14, 0, 0, 40, 40, 15, 30, 30);

        ButtonUI dropdownButton = new ButtonUI(400, 100, 200, 30, "Screen size", 13, 20.0f, Color.ORANGE);
        ArrayList<ButtonUI> dropdownItems = new ArrayList<>();
        dropdownItems.add(new ButtonUI(400, 50, 200, 30, "1920x1080", 13, 20.0f, Color.ORANGE));
        dropdownItems.add(new ButtonUI(400, 0, 200, 30, "1024x768", 13, 20.0f, Color.ORANGE));
        dropdownItems.add(new ButtonUI(400, -50, 200, 30, "620x480", 13, 20.0f, Color.ORANGE));
        Point[] dropdownData = new Point[]{new Point(1920, 1080), new Point(1024, 768), new Point(620, 480)};
        this.screenSizeDropdown = new DropdownUI<Point>(dropdownButton, dropdownItems, dropdownData);


        //int textureId, float x, float y, float width, float height, int sliderTextureId, float sliderX, float sliderY, float sliderWidth, float sliderHeight
        this.audioSlider = new SliderUI(13, 0, 300, 400, 40, 16, 0, 300, 30, 30, 1, 100);
    }
}
