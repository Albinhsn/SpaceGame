package se.liu.albhe576.project;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.glViewport;

public class SettingsMenuUI extends UI {
    private         UIState parentState;
    private final   ButtonUIComponent returnButton;
    private final   CheckboxUIComponent vsyncCheckbox;
    private final   DropdownUIComponent<Point> screenSizeDropdown;
    private final   SliderUIComponent audioSlider;
    public void setParentState(UIState uiState){
        this.parentState = uiState;
    }
    private void updateWindowSize(long window, Point newWindowSize){
        glfwSetWindowSize(window, newWindowSize.x, newWindowSize.y);
        Game.SCREEN_WIDTH = newWindowSize.x;
        Game.SCREEN_HEIGHT = newWindowSize.y;
        glViewport(0,0,newWindowSize.x, newWindowSize.y);
    }

    public UIState render(InputState inputState,Renderer renderer, long window, int score, int hp){

        renderer.renderButton(returnButton);
        returnButton.animate(inputState, 0.01f, 2.0f, Animation.easeOutCubic);
        if(returnButton.isReleased(inputState)){
            return this.parentState;
        }

        renderer.renderText("vsync", -25.0f, 0, ResourceManager.STATE_VARIABLES.get("fontSpaceSizeMedium"), ResourceManager.STATE_VARIABLES.get("fontFontSizeSmall"), Color.WHITE, true);
        renderer.renderCheckbox(this.vsyncCheckbox);
        if(vsyncCheckbox.isReleased(inputState)){
            vsyncCheckbox.toggled = !vsyncCheckbox.toggled;
            glfwSwapInterval(vsyncCheckbox.toggled ? 1 : 0);
        }

        if(this.screenSizeDropdown.toggled){
            for(int i = 0; i < this.screenSizeDropdown.dropdownData.length; i++){
                ButtonUIComponent item = this.screenSizeDropdown.dropdownItems.get(i);
                if(item.isReleased(inputState)){
                    this.updateWindowSize(window, this.screenSizeDropdown.dropdownData[i]);
                    this.screenSizeDropdown.toggled = false;
                    break;
                }
            }
        }

        // This happens after we attempt to render the items to avoid bugs with 1 click accidentally choosing the first item instantly
        renderer.renderDropdown(screenSizeDropdown.dropdownButton, screenSizeDropdown.toggled, screenSizeDropdown.dropdownItems);
        if(this.screenSizeDropdown.dropdownButton.isReleased(inputState)){
            this.screenSizeDropdown.toggled = !this.screenSizeDropdown.toggled;
        }

        renderer.renderSlider(this.audioSlider);
        if(this.audioSlider.isPressed(inputState)){
            this.audioSlider.updateSliderPosition(inputState.getMousePosition().x);
        }


        return UIState.SETTINGS_MENU;
    }
    public SettingsMenuUI(){
        final float buttonWidth                     = ResourceManager.STATE_VARIABLES.get("buttonSizeLargeWidth");
        final float buttonHeight                    = ResourceManager.STATE_VARIABLES.get("buttonSizeLargeHeight");
        final float spaceSizeSmall                  = ResourceManager.STATE_VARIABLES.get("fontSpaceSizeSmall");
        final float fontSize                        = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        final float spaceSize                       = ResourceManager.STATE_VARIABLES.get("fontSpaceSizeMedium");
        final float dropdownButtonWidth             = ResourceManager.STATE_VARIABLES.get("buttonSizeMediumWidth");
        final float dropdownButtonHeight            = ResourceManager.STATE_VARIABLES.get("buttonSizeSmallHeight");
        this.returnButton                           = new ButtonUIComponent(0.0f, -40.0f, buttonWidth, buttonHeight, "RETURN", spaceSize, fontSize);
        this.vsyncCheckbox                          = new CheckboxUIComponent(Texture.GREY_CHECKMARK_GREY, 0, 0, 6.0f, 8.0f, Texture.GREY_SLIDER_UP, 5.0f, 6.0f);


        String[] dropdownItemStrings    = new String[]{"1920x1080", "1600x900", "1024x768", "620x480"};
        Point[] dropdownData            = new Point[]{new Point(1920, 1080), new Point(1600, 900), new Point(1024, 768), new Point(620, 480)};
        this.screenSizeDropdown         = new DropdownUIComponent<Point>(60.0f, 20.0f, "Resolution", dropdownItemStrings, dropdownButtonWidth, dropdownButtonHeight, spaceSizeSmall, fontSize, dropdownData);
        this.audioSlider                = new SliderUIComponent(Texture.GREY_BOX, 0, 60.0f, 65.0f, 8.0f, Texture.GREY_SLIDER_HORIZONTAL, 5.0f, dropdownButtonHeight, 1, 100);
    }
}
