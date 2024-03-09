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
        returnButton.animate(inputState);
        if(returnButton.isReleased(inputState)){
            return this.parentState;
        }

        renderer.renderTextCenteredAt("vsync", -25.0f, 0, ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeMedium", 10.0f), ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeSmall", 3.0f), Color.WHITE);
        if(vsyncCheckbox.isReleased(inputState)){
            vsyncCheckbox.toggled = !vsyncCheckbox.toggled;
            glfwSwapInterval(vsyncCheckbox.toggled ? 1 : 0);
        }
        renderer.renderCheckbox(this.vsyncCheckbox);

        if(this.screenSizeDropdown.toggled){
            Point [] dropdownData = this.screenSizeDropdown.getDropdownData();
            int index = 0;
            for(ButtonUIComponent item : this.screenSizeDropdown.getDropdownItems()){
                if(item.isReleased(inputState)){
                    this.updateWindowSize(window, dropdownData[index]);
                    this.screenSizeDropdown.toggled = false;
                    break;
                }
                index++;
            }
        }

        // This happens after we attempt to render the items to avoid bugs with 1 click accidentally choosing the first item instantly
        if(this.screenSizeDropdown.getDropdownButton().isReleased(inputState)){
            this.screenSizeDropdown.toggled = !this.screenSizeDropdown.toggled;
        }
        renderer.renderDropdown(screenSizeDropdown);

        if(this.audioSlider.isPressed(inputState)){
            this.audioSlider.updateSliderPosition(inputState.getMousePosition().x);
        }
        renderer.renderSlider(this.audioSlider);


        return UIState.SETTINGS_MENU;
    }
    public SettingsMenuUI(){
        final float buttonWidth                     = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeLargeWidth", 40.0f);
        final float buttonHeight                    = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeLargeHeight", 10.0f);
        final float spaceSizeSmall                  = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);
        final float fontSize                        = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float spaceSize                       = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeMedium", 10.0f);
        final float dropdownButtonWidth             = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumWidth", 32.0f);
        final float dropdownButtonHeight            = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeSmallHeight", 6.0f);

        Animation returnButtonAnimation = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeOutCubic);
        this.returnButton                           = new ButtonUIComponent(0.0f, -40.0f, buttonWidth, buttonHeight, "RETURN", spaceSize, fontSize, returnButtonAnimation);
        this.vsyncCheckbox                          = new CheckboxUIComponent(Texture.GREY_CHECKMARK_GREY, 0, 0, 6.0f, 8.0f, Texture.GREY_SLIDER_UP, 5.0f, 6.0f);


        String[] dropdownItemStrings    = new String[]{"1920x1080", "1600x900", "1024x768", "620x480"};
        Point[] dropdownData            = new Point[]{new Point(1920, 1080), new Point(1600, 900), new Point(1024, 768), new Point(620, 480)};
        this.screenSizeDropdown         = new DropdownUIComponent<Point>(60.0f, 20.0f, "Resolution", dropdownItemStrings, dropdownButtonWidth, dropdownButtonHeight, spaceSizeSmall, fontSize, dropdownData);
        this.audioSlider                = new SliderUIComponent(Texture.GREY_BOX, 0, 60.0f, 65.0f, 8.0f, Texture.GREY_SLIDER_HORIZONTAL, 5.0f, dropdownButtonHeight, 1, 100);
    }
}
