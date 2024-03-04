package se.liu.albhe576.project;

import java.awt.*;

public class GameRunningUI extends UI{
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = 15.0f / Game.SCREEN_HEIGHT * 100.0f;
        renderer.renderTextStartAt(String.format("Score: %d", score), -100.0f, 100.0f - fontSize, fontSize, Color.WHITE);
        renderer.renderHealth(hp);
        return UIState.GAME_RUNNING;

    }
}
