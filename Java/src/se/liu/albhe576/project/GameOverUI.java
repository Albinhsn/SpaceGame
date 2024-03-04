package se.liu.albhe576.project;

import java.awt.*;

public class GameOverUI extends UI {

    private final ButtonUIComponent restartButton;
    private final ButtonUIComponent mainMenuButton;
    public boolean lostGame;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {

        renderer.renderTextCentered(lostGame  ? "GAME OVER" : "GAME WON", 0, 200.0f / Game.SCREEN_HEIGHT * 100.0f, 40 / (float)Game.SCREEN_HEIGHT * 100.0f, Color.WHITE);
        renderer.renderTextCentered(String.format("Score: %d", score), 0, 100 / (float)Game.SCREEN_HEIGHT * 100.0f, 40 / (float)Game.SCREEN_HEIGHT * 100.0f, Color.WHITE);

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
        this.restartButton     = new ButtonUIComponent(
                0.0f,
                0.0f,
                200.0f / Game.SCREEN_WIDTH * 100.0f,
                50.0f / Game.SCREEN_HEIGHT * 100.0f,
                "Restart Game",
                Texture.GREY_BOX,
                10.0f / Game.SCREEN_HEIGHT * 100.0f,
                Color.RED
        );
        this.mainMenuButton     = new ButtonUIComponent(
                0.0f,
                -100.0f / Game.SCREEN_HEIGHT * 100.0f,
                200.0f / Game.SCREEN_WIDTH * 100.0f,
                50.0f / Game.SCREEN_HEIGHT * 100.0f,
                "Main Menu",
                Texture.GREY_BOX,
                10.0f / Game.SCREEN_HEIGHT * 100.0f,
                Color.RED
        );
        this.lostGame = false;
    }
}
