package com.relhs.asianfinder.data;

public class MailMainItem {

	private String title;
	private int icon;
    private String imageurl;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;

	public MailMainItem(){}

    public MailMainItem(String title){
        this.title = title;
    }

//	public NavDrawerItem(String title, int icon){
//		this.title = title;
//		this.icon = icon;
//	}
    public MailMainItem(String title, String imageurl){
        setTitle(title);
        setUrl(imageurl);
    }

	public MailMainItem(String title, boolean isCounterVisible, String count){
		this.title = title;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}

    public String getUrl() {
        return imageurl;
    }

    public void setUrl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setCount(String count){
		this.count = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
