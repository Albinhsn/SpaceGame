package se.liu.albhe576.project;

import java.awt.*;

public class GameRunningUI extends UI{
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        final float fontSize = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        final float spaceSize = ResourceManager.STATE_VARIABLES.get("fontSpaceSizeMedium");

        renderer.renderText(String.format("Score: %d", score), -100.0f, 100.0f - fontSize, spaceSize, fontSize, Color.WHITE, false);
        renderer.renderHealth(hp);
        return UIState.GAME_RUNNING;

    }
}
