package se.liu.albhe576.project;

import java.awt.*;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class ConsoleUI implements UI{
    private final Logger logger = Logger.getLogger("Console");
    private String input;
    public UIState parent;
    private final int maxRenderedSentCommands = 7;
    private final String[] sentCommands = new String[maxRenderedSentCommands];
    private final UIComponent background;
    private final UIComponent consoleInput;
    private final long window;

    private void writeCommand(){
        for(int i = this.sentCommands.length - 2; i >= 0; i--){
            this.sentCommands[i + 1] = this.sentCommands[i];
        }
        this.sentCommands[0] = this.input;
        this.input = "";
    }

    private UIState handleInput(InputState inputState){
        boolean[] releasedState = inputState.getKeyboardStateReleased();
        for(int i = 0; i < releasedState.length; i++){
            if(releasedState[i]){
                this.input = this.input.concat(String.valueOf((char)i));
            }
        }

        if(inputState.isKeyReleased(GLFW_KEY_ENTER)){
            UIState out = this.executeCommands();
            if(out != null){
                return out;
            }
            this.writeCommand();
        }
        if(inputState.isKeyReleased(GLFW_KEY_BACKSPACE)){
            if(!this.input.isEmpty()){
                this.input = this.input.substring(0, this.input.length() - 1);
            }
        }
        if(inputState.isKeyReleased(GLFW_KEY_ESCAPE)){
            this.input = "";
            Arrays.fill(this.sentCommands, "");
            return this.parent;
        }
        return UIState.CONSOLE;
    }
    private void runListCommand(String commandValue){
        if(commandValue.equalsIgnoreCase("all")){
            for (Map.Entry<String, Float> entry : ResourceManager.STATE_VARIABLES.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }else{
            for (Map.Entry<String, Float> entry : ResourceManager.STATE_VARIABLES.entrySet()) {
                if(entry.getKey().equalsIgnoreCase(commandValue)){
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                    return ;
                }
            }
            logger.info(String.format("Couldn't find key '%s'\n", commandValue));
        }

    }
    private float parseFloatFromCommandValue(String commandValue){
        try{
            return (float)Double.parseDouble(commandValue);
        }catch(NumberFormatException | IllegalFormatConversionException e){
            logger.info(String.format("Failed to parse float from '%s'\n", commandValue));
        }
        return -1;

    }
    private void setStateVariable(String key, String commandValue){
        float value = this.parseFloatFromCommandValue(commandValue);
        logger.info(String.format("Set '%s' to %f", key, value));
        ResourceManager.STATE_VARIABLES.put(key, value);
    }
    private UIState executeCommands(){
        String[] splitInput = this.input.split(" ");
        String commandName = splitInput[0];
        String commandValue = splitInput.length > 1 ? splitInput[1] : "";
        switch(commandName){
            case "FONTSMALL":{
                this.setStateVariable("fontFontSizeSmall", commandValue);
                break;
            }
            case "FONTMEDIUM":{
                this.setStateVariable("fontFontSizeMedium", commandValue);
                break;
            }
            case "FONTLARGE":{
                this.setStateVariable("fontFontSizeLarge", commandValue);
                break;
            }
            case "MS":{
                this.setStateVariable("playerMS", commandValue);
                break;
            }
            case "DEBUG":{
                this.setStateVariable("debug", commandValue);
                break;
            }
            case "EXIT":{
                logger.info("Exiting game!");
                glfwSetWindowShouldClose(window, true);
                return this.parent;
            }
            case "GOD":{
                this.setStateVariable("godmode", commandValue);
                break;
            }
            case "LIST":{
                this.runListCommand(commandValue);
                break;
            }
            case "METEOR":{
                this.setStateVariable(commandValue, "numberOfMeteors");
                break;
            }
            case "RESTART":{
                ResourceManager.STATE_VARIABLES.put("restartWave", 1.0f);
                break;
            }
            case "WAVE":{
                this.setStateVariable(commandValue, "waveIdx");
                break;
            }
            default:{
                logger.info(String.format("Unknown command '%s', expected [NAME] [VARIABLE]", this.input));
                break;
            }
    }

        return null;
    }
    private void renderSentCommandsAndInput(Renderer renderer){
        float spaceSize = ResourceManager.STATE_VARIABLES.getOrDefault("fontSpaceSizeSmall", 5.0f);
        float fontSize  = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        final float x   = this.consoleInput.x - this.consoleInput.width;
        float y         = this.consoleInput.y;

        renderer.renderText(this.input, x, y, spaceSize, fontSize, Color.BLACK, TextLayoutEnum.STARTS_AT);
        for(String command : this.sentCommands){
            y += fontSize * 2;
            renderer.renderText(command, x, y, spaceSize, fontSize, Color.BLACK, TextLayoutEnum.STARTS_AT);
        }

    }
    public UIState render(InputState inputState, Renderer renderer, long window, int score, int hp) {
        renderer.renderUIComponent(background.textureId, background.x, background.y, background.width, background.height);
        renderer.renderUIComponent(consoleInput.textureId, consoleInput.x,consoleInput.y, consoleInput.width,consoleInput.height);

        this.renderSentCommandsAndInput(renderer);
        return this.handleInput(inputState);
    }

    public ConsoleUI(long window, ResourceManager resourceManager){
        this.background     = new UIComponent(0, 0, 50.0f, 50.0f, resourceManager.getTextureById(Texture.GREY_BUTTON_05).textureId);
        float fontSize      = ResourceManager.STATE_VARIABLES.getOrDefault("fontFontSizeMedium", 6.0f);
        this.consoleInput   = new UIComponent(0, -40.0f, 50.0f, fontSize * 1.5f, resourceManager.getTextureById(Texture.GREY_BUTTON_14).textureId);
        this.window         = window;
        this.input          = "";
        Arrays.fill(this.sentCommands, "");
    }
}
