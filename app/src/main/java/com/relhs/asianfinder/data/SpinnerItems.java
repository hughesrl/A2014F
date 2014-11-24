package com.relhs.asianfinder.data;

public class SpinnerItems {
    public String spinner_title;
    public String spinner_value;
    public Boolean status;

    public SpinnerItems() {

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
}
