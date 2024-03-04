package se.liu.albhe576.project;

import java.awt.*;

public class GameOverUI extends UI {

    private final ButtonUIComponent restartButton;
    private final ButtonUIComponent mainMenuButton;
    public boolean lostGame;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = ResourceManager.STATE_VARIABLES.get("fontSizeLarge");

        renderer.renderTextCentered(lostGame  ? "GAME OVER" : "GAME WON", 0, 40.0f, fontSize, Color.WHITE);
        renderer.renderTextCentered(String.format("Score: %d", score), 0, 20.0f, fontSize, Color.WHITE);

        renderer.renderButton(restartButton);
        if(restartButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(mainMenuButton);
        if(mainMenuButton.isReleased(inputState)){
            return UIState.MAIN_MENU;
        }

        return UIState.GAME_OVER_MENU;

    }

    public GameOverUI(){
        final float buttonWidth     = ResourceManager.STATE_VARIABLES.get("buttonSizeMediumWidth");
        final float buttonHeight    = ResourceManager.STATE_VARIABLES.get("buttonSizeMediumHeight");
        final float fontSize        = ResourceManager.STATE_VARIABLES.get("fontSizeSmall");

        this.restartButton          = new ButtonUIComponent(0.0f, 0.0f, buttonWidth, buttonHeight, "Restart Game", fontSize);
        this.mainMenuButton         = new ButtonUIComponent(0.0f, -2 * buttonHeight, buttonWidth, buttonHeight, "Main Menu", fontSize);
        this.lostGame               = false;
    }
}
