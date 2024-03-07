package se.liu.albhe576.project;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.util.Arrays;

public class ConsoleUI extends UI{
    // Commands
    //     loadWave X
    //     resetGame
    //     invincible?

    private String input;
    private UIState parent;
    private final String[] output = new String[7];
    private final UIComponent background;
    private final UIComponent consoleInput;

    public void setParentState(UIState parent){
        this.parent = parent;
    }
    @Override
    UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        // Render like some sort of quad background of grey thingy
        // Figure out how to render a input field at the bottom
        // figure out how to store input and render input
        // figure out how to execute commands :)

        renderer.renderUIComponent(background.textureId, background.x, background.y, background.width, background.height);
        renderer.renderUIComponent(consoleInput.textureId, consoleInput.x,consoleInput.y, consoleInput.width,consoleInput.height);
        boolean []releasedState = inputState.getKeyboardStateReleased();
        for(int i = 0; i < releasedState.length; i++){
            if(releasedState[i]){
                System.out.printf("Found '%c' %d\n", (char)i, i);
                this.input = this.input.concat(String.valueOf((char)i));
            }
        }
        if(inputState.isBackspaceReleased() && !this.input.isEmpty()){
            this.input = this.input.substring(0, this.input.length() - 1);
        }
        if(inputState.isEnterReleased()){
            if(this.input.equals("QUIT")){
                this.input = "";
                Arrays.fill(this.output, "");
                return this.parent;
            }
            for(int i = this.output.length - 2; i >= 0; i--){
                this.output[i + 1] = this.output[i];
            }
            this.output[0] = this.input;
            this.input = "";
        }


        if(!this.input.isEmpty()){
            renderer.renderText(this.input, -50.0f, -40.0f, ResourceManager.STATE_VARIABLES.get("fontSpaceSizeSmall"), ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium"), Color.BLACK, false);
        }

        for(int i = 0, y = -28; i < this.output.length; i++, y += 12){
            renderer.renderText(this.output[i], -50.0f, (float)y, ResourceManager.STATE_VARIABLES.get("fontSpaceSizeSmall"), ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium"), Color.BLACK, false);
        }

        return UIState.CONSOLE;
    }

    public ConsoleUI(ResourceManager resourceManager){
        this.background = new UIComponent(0, 0, 50.0f, 50.0f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_05).textureId);
        this.consoleInput = new UIComponent(0, -40.0f, 50.0f, 10.0f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_14).textureId);
        this.input = "";
        Arrays.fill(this.output, "");
    }
}
