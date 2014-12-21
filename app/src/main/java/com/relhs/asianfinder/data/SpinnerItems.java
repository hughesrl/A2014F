package com.relhs.asianfinder.data;

public class SpinnerItems {
    public String spinner_title;
    public String spinner_value;
    public Boolean status;
    public Boolean is_selected;

    public SpinnerItems(String spinner_title, String spinner_value, boolean status, boolean is_selected) {
        super();
        this.spinner_title = spinner_title;
        this.spinner_value = spinner_value;
        this.is_selected = is_selected;
    }

    public SpinnerItems(String spinner_title, String spinner_value, boolean status) {
        super();
        this.spinner_title = spinner_title;
        this.spinner_value = spinner_value;
        this.status = status;
    }

    public String getSpinnerTitle() {
        return spinner_title;
    }

    public String getSpinnerValue() {
        return spinner_value;
    }

    @Override
    public String toString() {
        return getSpinnerTitle();
    }

    public Boolean getSpinnerStatus() {
        return status;
    }

    public Boolean getIsSelected() {
        return is_selected;
    }

}
