package se.liu.albhe576.project;

import java.awt.*;

public class GameOverUI implements UI {
    private final ButtonUIComponent restartButton;
    private final ButtonUIComponent mainMenuButton;
    public boolean lostGame;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeLarge", 20.0f);
        final float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeLarge", 10.0f);

        renderer.renderText(lostGame  ? "GAME OVER" : "GAME WON", 0, 90.0f - fontSize, spaceSize, fontSize, Color.WHITE, TextLayoutEnum.CENTERED);
        renderer.renderText(String.format("Score: %d", score), 0, 90.0f - 3 * fontSize, spaceSize, fontSize, Color.WHITE, TextLayoutEnum.CENTERED);

        if(restartButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }
        restartButton.animate(inputState);
        renderer.renderButton(restartButton);

        if(mainMenuButton.isReleased(inputState)){
            return UIState.MAIN_MENU;
        }
        mainMenuButton.animate(inputState);
        renderer.renderButton(mainMenuButton);

        return UIState.GAME_OVER_MENU;

    }

    public GameOverUI(){
        final float buttonWidth     = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumWidth", 32.0f);
        final float buttonHeight    = ResourceManager.STATE_VARIABLES.getOrDefault("buttonSizeMediumHeight", 10.0f);
        final float fontSize        = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float spaceSize        = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);

        Animation restartButtonAnimation       = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeInCubic);
        this.restartButton          = new ButtonUIComponent(0.0f, -15.0f, buttonWidth, buttonHeight, "RESTART", spaceSize, fontSize, restartButtonAnimation);
        Animation mainMenuButtonAnimation       = new Animation(buttonWidth, buttonHeight, 500, 2.0f, Animation.easeOutCubic);
        this.mainMenuButton         = new ButtonUIComponent(0.0f, -46.0f, buttonWidth, buttonHeight, "MAIN MENU", spaceSize, fontSize, mainMenuButtonAnimation);
        this.lostGame               = false;
    }
}
