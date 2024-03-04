package se.liu.albhe576.project;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainMenuUI extends UI{

    private final long window;
    private final ButtonUIComponent playButton;
    private final ButtonUIComponent exitButton;
    private final ButtonUIComponent settingsButton;

    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp){

        renderer.renderButton(playButton);
        playButton.animate(inputState, 0.005f, 10.0f / Game.SCREEN_HEIGHT * 100.0f, Animation.easeInCubic);
        if(playButton.isReleased(inputState)){
            return UIState.GAME_RUNNING;
        }

        renderer.renderButton(exitButton);
        exitButton.animate(inputState, 0.005f, 10.0f / Game.SCREEN_HEIGHT * 100.0f, Animation.easeLinearly);
        if(exitButton.isReleased(inputState)){
            glfwSetWindowShouldClose(this.window, true);
            return UIState.MAIN_MENU;
        }

        renderer.renderButton(settingsButton);
        settingsButton.animate(inputState, 0.005f, 10.0f  / Game.SCREEN_HEIGHT * 100.0f, Animation.easeOutCubic);
        if(settingsButton.isReleased(inputState)){
            return UIState.SETTINGS_MENU;
        }

        return UIState.MAIN_MENU;
    }
    public MainMenuUI(long window){
        this.window         = window;
        this.playButton     = new ButtonUIComponent(
                0.0f,
                (150.0f / Game.SCREEN_HEIGHT) * 100.0f,
                200.0f / Game.SCREEN_WIDTH * 100.0f,
                50.0f / Game.SCREEN_HEIGHT * 100.0f,
                "Play",
                Texture.GREY_BOX,
                20.0f / Game.SCREEN_HEIGHT * 100.0f,
                Color.RED
        );

        this.exitButton     = new ButtonUIComponent(
                0.0f,
                -150.0f / Game.SCREEN_HEIGHT * 100.0f,
                200.0f/ Game.SCREEN_WIDTH * 100.0f,
                50.0f/ Game.SCREEN_HEIGHT * 100.0f,
                "Exit",
                Texture.GREY_BOX,
                20.0f / Game.SCREEN_HEIGHT * 100.0f,
                Color.RED
        );
        this.settingsButton     = new ButtonUIComponent(
                0.0f,
                0.0f,
                200.0f / Game.SCREEN_WIDTH * 100.0f,
                50.0f / Game.SCREEN_HEIGHT * 100.0f,
                "Settings",
                Texture.GREY_BOX,
                20.0f / Game.SCREEN_HEIGHT * 100.0f,
                Color.RED
        );

    }
}
