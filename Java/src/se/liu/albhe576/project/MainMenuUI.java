package se.liu.albhe576.project;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainMenuUI extends UI{
    private final long window;
    private final ButtonUIComponent playButton;
    private final ButtonUIComponent exitButton;
    private final ButtonUIComponent settingsButton;

    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp){

        renderer.renderButton(playButton);
        playButton.animate(inputState, 0.005f, 2.0f, Animation.easeInCubic);
        if(playButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(exitButton);
        exitButton.animate(inputState, 0.005f, 2.0f, Animation.easeLinearly);
        if(exitButton.isReleased(inputState)){
            glfwSetWindowShouldClose(this.window, true);
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        settingsButton.animate(inputState, 0.005f, 2.0f, Animation.easeOutCubic);
        if(settingsButton.isReleased(inputState)){
            return UIState.SETTINGS_MENU;
        }

        return UIState.MAIN_MENU;
    }
    public MainMenuUI(long window){
        final float buttonWidth     = ResourceManager.STATE_VARIABLES.get("buttonSizeLargeWidth");
        final float buttonHeight    = ResourceManager.STATE_VARIABLES.get("buttonSizeLargeHeight");
        final float buttonFontSize  = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        final float buttonSpaceSize = ResourceManager.STATE_VARIABLES.get("fontSpaceSizeMedium");

        this.window                 = window;
        this.playButton             = new ButtonUIComponent(0.0f, 31.0f, buttonWidth, buttonHeight, "Play", buttonSpaceSize, buttonFontSize);
        this.settingsButton         = new ButtonUIComponent(0.0f, 0.0f, buttonWidth, buttonHeight, "Settings", buttonSpaceSize, buttonFontSize);
        this.exitButton             = new ButtonUIComponent(0.0f, -31.0f, buttonWidth, buttonHeight, "Exit", buttonSpaceSize, buttonFontSize);
    }
}
