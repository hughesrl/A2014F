package com.relhs.asianfinder.data;

public class UserDetailsMultipleSelectionModel {
    private String value;
    private String caption;
    private String selectionType;
    private boolean selected;

    public UserDetailsMultipleSelectionModel(String value, String caption, String selectionType, boolean isSelected) {
        this.value = value;
        this.caption = caption;
        this.selectionType = selectionType;
        selected = isSelected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
