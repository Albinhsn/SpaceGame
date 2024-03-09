package se.liu.albhe576.project;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
public class MainMenuUI extends UI{
    private final long window;
    private final ButtonUIComponent playButton;
    private final ButtonUIComponent exitButton;
    private final ButtonUIComponent settingsButton;

    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp){

        if(playButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }
        playButton.animate(inputState);
        renderer.renderButton(playButton);

        if(exitButton.isReleased(inputState)){
            glfwSetWindowShouldClose(this.window, true);
            return UIState.MAIN_MENU;
        }
        exitButton.animate(inputState);
        renderer.renderButton(exitButton);

        if(settingsButton.isReleased(inputState)){
            return UIState.SETTINGS_MENU;
        }
        settingsButton.animate(inputState);
        renderer.renderButton(settingsButton);

        return UIState.MAIN_MENU;
    }
    public MainMenuUI(long window){
        final float buttonWidth             = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeLargeWidth", 40.0f);
        final float buttonHeight            = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeLargeHeight", 10.0f);
        final float buttonFontSize          = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float buttonSpaceSize         = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeMedium", 10.0f);

        this.window                         = window;
        Animation playButtonAnimation       = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeInCubic);
        this.playButton                     = new ButtonUIComponent(0.0f, 31.0f, buttonWidth, buttonHeight, "PLAY", buttonSpaceSize, buttonFontSize, playButtonAnimation);

        Animation settingsButtonAnimation   = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeLinearly);
        this.settingsButton                 = new ButtonUIComponent(0.0f, 0.0f, buttonWidth, buttonHeight, "SETTINGS", buttonSpaceSize, buttonFontSize, settingsButtonAnimation);

        Animation exitButtonAnimation       = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeOutCubic);
        this.exitButton                     = new ButtonUIComponent(0.0f, -31.0f, buttonWidth, buttonHeight, "EXIT", buttonSpaceSize, buttonFontSize, exitButtonAnimation);
    }
}
