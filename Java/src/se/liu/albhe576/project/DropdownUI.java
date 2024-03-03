package se.liu.albhe576.project;

import java.util.ArrayList;
import java.util.List;

public class DropdownUI<T> {
    public boolean toggled;
    public final List<ButtonUI> dropdownItems;
    public final ButtonUI dropdownButton;
    public final T[] dropdownData;
    public DropdownUI(ButtonUI dropdownButton, List<ButtonUI> dropdownItems, T[] dropdownData){
        this.toggled            = false;
        this.dropdownItems      = dropdownItems;
        this.dropdownButton     = dropdownButton;
        this.dropdownData       = dropdownData;
    }
}
