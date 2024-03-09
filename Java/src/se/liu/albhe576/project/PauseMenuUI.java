package se.liu.albhe576.project;

public class PauseMenuUI extends UI {
    private final ButtonUIComponent playButton;
    private final ButtonUIComponent mainMenuButton;
    private final ButtonUIComponent settingsButton;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {

        renderer.renderButton(playButton);
        if(playButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(mainMenuButton);
        if(mainMenuButton.isReleased(inputState)){
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        if(settingsButton.isReleased(inputState)){
            return UIState.SETTINGS_MENU;
        }

        return UIState.PAUSE_MENU;

    }
    public PauseMenuUI(){
        final float buttonWidth     = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumWidth", 32.0f);
        final float buttonHeight    = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumHeight", 10.0f);
        final float buttonFontSize  = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float buttonSpaceSize  = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeMedium", 10.0f);

        this.playButton             = new ButtonUIComponent(0.0f, 2.0f * buttonHeight, buttonWidth, buttonHeight, "PLAY",buttonSpaceSize, buttonFontSize);
        this.settingsButton         = new ButtonUIComponent(0.0f, 0.0f, buttonWidth, buttonHeight, "SETTINGS",  buttonSpaceSize, buttonFontSize);
        this.mainMenuButton         = new ButtonUIComponent(0.0f, -2.0f * buttonHeight, buttonWidth, buttonHeight, "MAIN MENU", buttonSpaceSize, buttonFontSize);
    }
}
