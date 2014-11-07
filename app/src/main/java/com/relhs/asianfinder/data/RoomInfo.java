package com.relhs.asianfinder.data;

public class RoomInfo {
    private int userId;
    private int threadId;
    private String userType;
    private String userName;
    private String main_photo;
    private int isChatting;
    private String lastOnline;
    private String date;


    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getIsChatting() {
        return isChatting;
    }

    public void setIsChatting(int isChatting) {
        this.isChatting = isChatting;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public void setMain_photo(String main_photo) {
        this.main_photo = main_photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
