package com.relhs.asianfinder.data;

public class UserDetailsInfo {
	private int id;
    private String category;
    private String dbname;
    private String label;
    private String type;
    private String value;
    private String ids;
    private Boolean isSection;

    public UserDetailsInfo() {
        setIsSection(false);
    }

    public UserDetailsInfo(String category) {
        setCategory(category.replace("_"," "));
        setIsSection(true);
    }
    public UserDetailsInfo(String dbname, String label, String type, String value, String ids) {
        setDbname(dbname);
        setLabel(label);
        setType(type);
        setValue(value);
        setIds(ids);
        setIsSection(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Boolean getIsSection() {
        return isSection;
    }

    public void setIsSection(Boolean isSection) {
        this.isSection = isSection;
    }
}
