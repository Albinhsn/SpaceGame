package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

public class DropdownUIComponent<T> {
    public boolean toggled;
    private final List<ButtonUIComponent> dropdownItems;
    private final ButtonUIComponent dropdownButton;
    private final T[] dropdownData;
    public List<ButtonUIComponent> getDropdownItems(){return this.dropdownItems;}
    public ButtonUIComponent getDropdownButton(){return this.dropdownButton;}
    public T[] getDropdownData(){return this.dropdownData;}
    public DropdownUIComponent(float x, float y, String buttonString, String[]itemStrings, float buttonWidth, float buttonHeight, float spaceSize, float fontSize, T[] itemData){
        this.toggled        = false;
        this.dropdownData   = itemData;
        this.dropdownButton = new ButtonUIComponent(x, y, buttonWidth, buttonHeight, buttonString, spaceSize, fontSize);
        this.dropdownItems  = new ArrayList<>();

        for(int i = 0; i < itemStrings.length; i++){
            String itemString = itemStrings[i];
            this.dropdownItems.add(new ButtonUIComponent(x, y - (i + 1) *fontSize * 2.0f, buttonWidth, buttonHeight, itemString, spaceSize, fontSize));
        }
    }
}
