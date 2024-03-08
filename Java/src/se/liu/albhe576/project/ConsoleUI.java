package se.liu.albhe576.project;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ConsoleUI extends UI{
    // Commands
    //     loadWave X
    //     resetGame
    //     invincible?

    private final Logger logger = Logger.getLogger("Console");
    private String input;
    private UIState parent;
    private final String[] sentCommands = new String[7];
    private final UIComponent background;
    private final UIComponent consoleInput;
    private final long window;

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
        if(this.input.startsWith("DEBUG")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2) {
                try{
                    int debug = Integer.parseInt(splitInput[1]);
                    ResourceManager.STATE_VARIABLES.put("debug", (float)debug);
                    return null;
                }catch(NumberFormatException e){
                    logger.info(String.format("Failed to parse int from '%s'\n", splitInput[1]));
                }
            }
        }
        if(this.input.startsWith("INVINCIBLE")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2) {
                try{
                    int invincible = Integer.parseInt(splitInput[1]);
                    ResourceManager.STATE_VARIABLES.put("invincible", (float)invincible);
                    return null;
                }catch(NumberFormatException e){
                    logger.info(String.format("Failed to parse int from '%s'\n", splitInput[1]));
                }
            }
        }
        if(this.input.startsWith("RESTART")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2) {
                if (splitInput[1].equalsIgnoreCase("wave")) {
                    logger.info("Restart wave command sent!");
                    ResourceManager.STATE_VARIABLES.put("restartWave", 1.0f);
                    return null;
                }
            }

        }
        else if(this.input.startsWith("LIST")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2){
                    if(splitInput[1].equalsIgnoreCase("all")){
                        for (Map.Entry<String, Float> entry : ResourceManager.STATE_VARIABLES.entrySet()) {
                            System.out.println(entry.getKey() + ": " + entry.getValue());
                        }
                    }else{
                        for (Map.Entry<String, Float> entry : ResourceManager.STATE_VARIABLES.entrySet()) {
                            if(entry.getKey().equalsIgnoreCase(splitInput[1])){
                                System.out.println(entry.getKey() + ": " + entry.getValue());
                                return null;
                            }
                        }
                        System.out.printf("Couldn't find key '%s'\n", splitInput[1]);
                    }
                    return null;
            }

        }
        if(this.input.startsWith("WAVE")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2){
                try{
                    int nextWave = Integer.parseInt(splitInput[1]);
                    ResourceManager.STATE_VARIABLES.put("waveIdx", (float)nextWave);
                    return null;
                }catch(NumberFormatException e){
                    logger.info(String.format("Failed to parse int from '%s'\n", splitInput[1]));
                }
            }
        }
        if(this.input.startsWith("METEOR")){
            String[] splitInput = this.input.split(" ");
            if(splitInput.length == 2){
                try{
                    int nextWave = Integer.parseInt(splitInput[1]);
                    ResourceManager.STATE_VARIABLES.put("numberOfMeteors", (float)nextWave);
                    return null;
                }catch(NumberFormatException e){
                    logger.info(String.format("Failed to parse int from '%s'\n", splitInput[1]));
                }
            }
        }
        if(this.input.equals("EXIT")) {
            logger.info("Exiting game!");
            glfwSetWindowShouldClose(window, true);
            return this.parent;
        }
        if(this.input.equals("QUIT")){
            this.input = "";
            Arrays.fill(this.sentCommands, "");
            logger.info("Returning to " + this.parent);
            return this.parent;
        }
        logger.info(String.format("Unknown command '%s'", this.input));
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
        if(inputState.isBackspaceReleased()){
            if(!this.input.isEmpty()){
                this.input = this.input.substring(0, this.input.length() - 1);
            }
        }
        if(inputState.isEscapeReleased()){
            return this.parent;
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

    public ConsoleUI(long window, ResourceManager resourceManager){
        this.background     = new UIComponent(0, 0, 50.0f, 50.0f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_05).textureId);
        float fontSize      = ResourceManager.STATE_VARIABLES.get("fontFontSizeMedium");
        this.consoleInput   = new UIComponent(0, -40.0f, 50.0f, fontSize * 1.5f, resourceManager.textureIdMap.get(Texture.GREY_BUTTON_14).textureId);
        this.window         = window;
        this.input          = "";
        Arrays.fill(this.sentCommands, "");
    }
}
