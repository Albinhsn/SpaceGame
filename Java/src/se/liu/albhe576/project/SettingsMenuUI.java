package se.liu.albhe576.project;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.glViewport;

public class SettingsMenuUI extends UI {
    private UIState parentState;
    private final ButtonUI returnButton;
    private final CheckboxUI vsyncCheckbox;
    private final DropdownUI<Point> screenSizeDropdown;

    private final SliderUI audioSlider;

    public void setParentState(UIState uiState){
        this.parentState = uiState;
    }

    public UIState render(InputState inputState,Renderer renderer, long window, int score, int hp){

        renderer.renderButton(returnButton);
        returnButton.animateEaseOutCubic(inputState, 0.01f, 10.0f);
        if(returnButton.isReleased(inputState)){
            return this.parentState;
        }

        renderer.renderTextCentered("vsync", -Game.SCREEN_WIDTH * 0.2f, 0, Game.SCREEN_HEIGHT * 0.04f, Color.WHITE);

        renderer.renderCheckbox(this.vsyncCheckbox);
        if(vsyncCheckbox.isReleased(inputState)){
            vsyncCheckbox.toggled = !vsyncCheckbox.toggled;
            glfwSwapInterval(vsyncCheckbox.toggled ? 1 : 0);
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
                    glViewport(0,0,Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
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
        this.returnButton       = new ButtonUI(
                0.0f,
                -Game.SCREEN_HEIGHT * 0.4f,
                Game.SCREEN_WIDTH * 0.4f,
                Game.SCREEN_HEIGHT * 0.1f,
                "Return",
                Texture.GREY_BOX,
                20.0f, Color.RED
        );
        this.vsyncCheckbox      = new CheckboxUI(
                Texture.GREY_CHECKMARK_GREY,
                0,
                0,
                Game.SCREEN_WIDTH * 0.06f,
                Game.SCREEN_HEIGHT * 0.08f,
                Texture.GREY_SLIDER_UP,
                // This is scuffed because of aspect ratio
                Game.SCREEN_WIDTH * 0.05f,
                Game.SCREEN_HEIGHT * 0.06f
        );

        ButtonUI dropdownButton = new ButtonUI(
                Game.SCREEN_WIDTH * 0.6f,
                Game.SCREEN_HEIGHT * 0.2f,
                Game.SCREEN_WIDTH * 0.3f,
                Game.SCREEN_HEIGHT * 0.06f,
                "Screen size",
                Texture.GREY_BOX,
                Game.SCREEN_HEIGHT * 0.04f,
                Color.RED
        );
        ArrayList<ButtonUI> dropdownItems = new ArrayList<>();
        dropdownItems.add(new ButtonUI(
                Game.SCREEN_WIDTH * 0.6f,
                Game.SCREEN_HEIGHT * 0.1f,
                Game.SCREEN_WIDTH * 0.3f,
                Game.SCREEN_HEIGHT * 0.06f,
                "1920x1080",
                Texture.GREY_BOX,
                Game.SCREEN_HEIGHT * 0.04f,
                Color.RED
        ));

        dropdownItems.add(new ButtonUI(
                Game.SCREEN_WIDTH * 0.6f,
                0,
                Game.SCREEN_WIDTH * 0.3f,
                Game.SCREEN_HEIGHT * 0.06f,
                "1600x900",
                Texture.GREY_BOX,
                Game.SCREEN_HEIGHT * 0.04f,
                Color.RED
        ));

        dropdownItems.add(new ButtonUI(
                Game.SCREEN_WIDTH * 0.6f,
                -Game.SCREEN_HEIGHT * 0.1f,
                Game.SCREEN_WIDTH * 0.3f,
                Game.SCREEN_HEIGHT * 0.06f,
                "1024x768",
                Texture.GREY_BOX,
                Game.SCREEN_HEIGHT * 0.04f,
                Color.RED
        ));

        dropdownItems.add(new ButtonUI(
                Game.SCREEN_WIDTH * 0.6f,
                -Game.SCREEN_HEIGHT * 0.2f,
                Game.SCREEN_WIDTH * 0.3f,
                Game.SCREEN_HEIGHT * 0.06f,
                "620x480",
                Texture.GREY_BOX,
                Game.SCREEN_HEIGHT * 0.04f,
                Color.RED
        ));
        Point[] dropdownData = new Point[]{new Point(1920, 1080), new Point(1600, 900), new Point(1024, 768), new Point(620, 480)};
        this.screenSizeDropdown = new DropdownUI<>(dropdownButton, dropdownItems, dropdownData);


        //int textureId, float x, float y, float width, float height, int sliderTextureId, float sliderX, float sliderY, float sliderWidth, float sliderHeight
        this.audioSlider = new SliderUI(
                Texture.GREY_BOX,
                0,
                Game.SCREEN_HEIGHT * 0.6f,
                Game.SCREEN_WIDTH * 0.65f,
                Game.SCREEN_HEIGHT * 0.08f,
                Texture.GREY_SLIDER_HORIZONTAL,
                0,
                Game.SCREEN_HEIGHT * 0.6f,
                Game.SCREEN_WIDTH * 0.05f,
                Game.SCREEN_HEIGHT * 0.06f,
                1,
                100
        );
    }
}
