package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 */
public class DropdownUIComponent<T> {
    /**
     *
     */
    public boolean toggled;
    /**
     *
     */
    public final List<ButtonUIComponent> dropdownItems;
    /**
     *
     */
    public final ButtonUIComponent dropdownButton;
    /**
     *
     */
    public final T[] dropdownData;

    /**
     * @param x
     * @param y
     * @param buttonString
     * @param itemStrings
     * @param buttonWidth
     * @param buttonHeight
     * @param spaceSize
     * @param fontSize
     * @param itemData
     */
    public DropdownUIComponent(float x, float y, String buttonString, String[]itemStrings, float buttonWidth, float buttonHeight, float spaceSize, float fontSize, T[] itemData){
        this.toggled        = false;
        this.dropdownButton =  new ButtonUIComponent(x, y, buttonWidth, buttonHeight, buttonString, spaceSize, fontSize);
        this.dropdownItems = new ArrayList<>();
        for(int i = 0; i < itemStrings.length; i++){
            String itemString = itemStrings[i];
            this.dropdownItems.add(new ButtonUIComponent(x, y - (i + 1) *fontSize * 2.0f, buttonWidth, buttonHeight, itemString, spaceSize, fontSize));

        }
        this.dropdownData = itemData;

    }

    /**
     * @param dropdownButton
     * @param dropdownItems
     * @param dropdownData
     */
    public DropdownUIComponent(ButtonUIComponent dropdownButton, List<ButtonUIComponent> dropdownItems, T[] dropdownData){
        this.toggled            = false;
        this.dropdownItems      = dropdownItems;
        this.dropdownButton     = dropdownButton;
        this.dropdownData       = dropdownData;
    }
}
