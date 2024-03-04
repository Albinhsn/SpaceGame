package se.liu.albhe576.project;

import java.awt.*;

public class PauseMenuUI extends UI {
    private final ButtonUIComponent playButton;
    private final ButtonUIComponent mainMenuButton;
    private final ButtonUIComponent settingsButton;
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {

        renderer.renderButton(playButton);
        if(playButton.isReleased(inputState)){
            System.out.println("Play!");
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(mainMenuButton);
        if(mainMenuButton.isReleased(inputState)){
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        if(settingsButton.isReleased(inputState)){
            System.out.println("Settings!");
            return UIState.SETTINGS_MENU;
        }

        return UIState.PAUSE_MENU;

    }
    public PauseMenuUI(){
        this.playButton     = new ButtonUIComponent(0.0f, 20.0f, 32.0f, 10.0f, "Play", Texture.GREY_BOX, 4.0f, Color.ORANGE);
        this.settingsButton     = new ButtonUIComponent(0.0f, 0.0f, 32.0f, 10.0f, "Settings", Texture.GREY_BOX, 4.0f, Color.ORANGE);
        this.mainMenuButton     = new ButtonUIComponent(0.0f, -20.0f, 32.0f, 10.0f, "Main Menu", Texture.GREY_BOX, 4.0f, Color.ORANGE);

    }
}
