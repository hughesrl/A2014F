package com.relhs.asianfinder.data;

import com.relhs.asianfinder.view.Item;

public class ProfilePreferenceDataInfo implements Item{
    private String dbname;
    private String label;
    private String type;
    private String value;
    private String ids;

    public ProfilePreferenceDataInfo(String dbname, String label, String type, String value, String ids) {
        setDbname(dbname);
        setLabel(label);
        setType(type);
        setValue(value);
        setIds(ids);
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
