package se.liu.albhe576.project;

import java.util.List;

public class DropdownUIComponent<T> {
    public boolean toggled;
    public final List<ButtonUIComponent> dropdownItems;
    public final ButtonUIComponent dropdownButton;
    public final T[] dropdownData;
    public DropdownUIComponent(ButtonUIComponent dropdownButton, List<ButtonUIComponent> dropdownItems, T[] dropdownData){
        this.toggled            = false;
        this.dropdownItems      = dropdownItems;
        this.dropdownButton     = dropdownButton;
        this.dropdownData       = dropdownData;
    }
}
