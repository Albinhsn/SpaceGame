package se.liu.albhe576.project;

import java.awt.*;

public class GameOverUI extends UI {
    private final ButtonUIComponent restartButton;
    private final ButtonUIComponent mainMenuButton;
    public boolean lostGame;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeLarge", 20.0f);
        final float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeLarge", 10.0f);

        renderer.renderTextCenteredAt(lostGame  ? "GAME OVER" : "GAME WON", 0, 90.0f - fontSize, spaceSize, fontSize, Color.WHITE);
        renderer.renderTextCenteredAt(String.format("Score: %d", score), 0, 90.0f - 3 * fontSize, spaceSize, fontSize, Color.WHITE);

        if(restartButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }
        renderer.renderButton(restartButton);

        if(mainMenuButton.isReleased(inputState)){
            return UIState.MAIN_MENU;
        }
        renderer.renderButton(mainMenuButton);

        return UIState.GAME_OVER_MENU;

    }

    public GameOverUI(){
        final float buttonWidth     = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumWidth", 32.0f);
        final float buttonHeight    = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumHeight", 10.0f);
        final float fontSize        = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float spaceSize        = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);

        this.restartButton          = new ButtonUIComponent(0.0f, 0.0f, buttonWidth, buttonHeight, "RESTART", spaceSize, fontSize);
        this.mainMenuButton         = new ButtonUIComponent(0.0f, -2 * buttonHeight, buttonWidth, buttonHeight, "MAIN MENU", spaceSize, fontSize);
        this.lostGame               = false;
    }
}
