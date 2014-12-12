package com.relhs.asianfinder.data;

import com.relhs.asianfinder.view.Item;

public class ProfilePreferenceHeaderInfo implements Item {
    private String preferenceHeader;

    public ProfilePreferenceHeaderInfo(String preferenceHeader) {
        setPreferenceHeader(preferenceHeader);
    }

    public String getPreferenceHeader() {
        return preferenceHeader;
    }

    public void setPreferenceHeader(String preferenceHeader) {
        this.preferenceHeader = preferenceHeader;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
