package se.liu.albhe576.project;

import java.awt.*;

public class GameRunningUI extends UI{
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        renderer.renderTextStartAt(String.format("Score: %d", score), -Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT - 15, 15, Color.WHITE);
        renderer.renderHealth(hp);
        return UIState.GAME_RUNNING;

    }
}
