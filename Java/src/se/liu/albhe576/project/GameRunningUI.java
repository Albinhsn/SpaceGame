package se.liu.albhe576.project;

import java.awt.*;

public class GameRunningUI extends UI{
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeMedium", 10.0f);

        renderer.renderTextStartsAt(String.format("Score: %d", score), -100.0f, 100.0f - fontSize, spaceSize, fontSize, Color.WHITE);
        renderer.renderHealth(hp);
        return UIState.GAME_RUNNING;

    }
}
