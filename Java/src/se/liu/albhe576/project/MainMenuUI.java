package se.liu.albhe576.project;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainMenuUI extends UI{

    private final long window;
    private final ButtonUI playButton;
    private final ButtonUI exitButton;
    private final ButtonUI settingsButton;

    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp){

        renderer.renderButton(playButton);
        if(playButton.isReleased(inputState)){
            System.out.println("Play!");
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(exitButton);
        if(exitButton.isReleased(inputState)){
            System.out.println("Exiting!");
            glfwSetWindowShouldClose(this.window, true);
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        if(settingsButton.isReleased(inputState)){
            System.out.println("Settings!");
            return UIState.SETTINGS_MENU;
        }

        return UIState.MAIN_MENU;
    }
    public MainMenuUI(long window){
        this.window         = window;
        this.playButton     = new ButtonUI(0.0f, 100.0f, 200.0f, 50.0f, "Play", 13, 20.0f, Color.ORANGE);
        this.exitButton     = new ButtonUI(0.0f, -100.0f, 200.0f, 50.0f, "Exit", 13, 20.0f, Color.ORANGE);
        this.settingsButton     = new ButtonUI(0.0f, 0.0f, 200.0f, 50.0f, "Settings", 13, 20.0f, Color.ORANGE);

    }
}
