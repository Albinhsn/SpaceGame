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
    private final String[] sentCommands = new String[7];
    private final UIComponent background;
    private final UIComponent consoleInput;

    public void setParentState(UIState parent){
        this.parent = parent;
    }

    private void writeCommand(){
        for(int i = this.sentCommands.length - 2; i >= 0; i--){
            this.sentCommands[i + 1] = this.sentCommands[i];
        }
        this.sentCommands[0] = this.input;
        this.input = "";
    }

    private void addInput(boolean [] releasedState){
        for(int i = 0; i < releasedState.length; i++){
            if(releasedState[i]){
                this.input = this.input.concat(String.valueOf((char)i));
            }
        }
    }
    private UIState executeCommands(){
        if(this.input.equals("QUIT")){
            this.input = "";
            Arrays.fill(this.sentCommands, "");
            return this.parent;
        }
        return null;
    }
    @Override
    UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        renderer.renderUIComponent(background.textureId, background.x, background.y, background.width, background.height);
        renderer.renderUIComponent(consoleInput.textureId, consoleInput.x,consoleInput.y, consoleInput.width,consoleInput.height);

        float spaceSize = ResourceManager.STATE_VARIABLES.get("fontSpaceSizeSmall");
        float fontSize  = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        final float x   = this.consoleInput.x - this.consoleInput.width;
        float y         = this.consoleInput.y;

        this.addInput(inputState.getKeyboardStateReleased());
        if(inputState.isEnterReleased()){
            UIState out = this.executeCommands();
            if(out != null){
                return out;
            }
            this.writeCommand();
        }


        if(!this.input.isEmpty()){
            renderer.renderText(this.input, x, y, spaceSize, fontSize, Color.BLACK, false);
        }

        for(String command : this.sentCommands){
            y += fontSize * 2;
            renderer.renderText(command, x, y, spaceSize, fontSize, Color.BLACK, false);
        }

        return UIState.CONSOLE;
    }

    public ConsoleUI(ResourceManager resourceManager){
        this.background     = new UIComponent(0, 0, 50.0f, 50.0f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_05).textureId);
        float fontSize  = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        this.consoleInput   = new UIComponent(0, -40.0f, 50.0f, fontSize * 1.5f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_14).textureId);
        this.input          = "";
        Arrays.fill(this.sentCommands, "");
    }
}
