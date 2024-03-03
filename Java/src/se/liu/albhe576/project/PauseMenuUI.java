package se.liu.albhe576.project;

import java.awt.*;

public class PauseMenuUI extends UI {
    private final ButtonUI playButton;
    private final ButtonUI mainMenuButton;
    private final ButtonUI settingsButton;
    public UIState render(InputState inputState, Renderer renderer) {

        renderer.renderButton(playButton);
        if(playButton.isPressed(inputState)){
            System.out.println("Play!");
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(mainMenuButton);
        if(mainMenuButton.isPressed(inputState)){
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        if(settingsButton.isPressed(inputState)){
            System.out.println("Settings!");
            return UIState.SETTINGS_MENU;
        }

        return UIState.PAUSE_MENU;

    }
    public PauseMenuUI(){
        this.playButton     = new ButtonUI(0.0f, 100.0f, 200.0f, 50.0f, "Play", 13, 20.0f, Color.ORANGE);
        this.mainMenuButton     = new ButtonUI(0.0f, -100.0f, 200.0f, 50.0f, "Main Menu", 13, 20.0f, Color.ORANGE);
        this.settingsButton     = new ButtonUI(0.0f, 0.0f, 200.0f, 50.0f, "Settings", 13, 20.0f, Color.ORANGE);
    }
}
