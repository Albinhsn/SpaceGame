package se.liu.albhe576.project;

import java.awt.*;

public class SettingsMenuUI extends UI {
    private UIState parentState;
    private final ButtonUI returnButton;
    public void setParentState(UIState uiState){
        this.parentState = uiState;
    }

    public UIState render(InputState inputState, Renderer renderer){

        renderer.renderButton(returnButton);
        if(returnButton.isPressed(inputState)){
            System.out.println("Return");
            return this.parentState;
        }

        return UIState.SETTINGS_MENU;
    }
    public SettingsMenuUI(){
        this.returnButton     = new ButtonUI(0.0f, -100.0f, 200.0f, 50.0f, "Return", 13, 20.0f, Color.ORANGE);
    }
}
