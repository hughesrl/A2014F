package com.relhs.asianfinder.data;

public class ThreadsInfo {
    private int threadId;
    private int f;
    private int localId;
    private String message;
    private int t;
    private int isSeen;
    private String messageType;
    private String folder_sticker;
    private String file_sticker;

    public ThreadsInfo(){

    }
    public ThreadsInfo(int threadId, int f, int localId, String message, int t, int isSeen, String messageType, String folder_sticker, String file_sticker) {
        super();
        setThreadId(threadId);
        setF(f);
        setLocalId(localId);
        setMessage(message);
        setT(t);
        setIsSeen(isSeen);
        setMessageType(messageType);
        setFolderSticker(folder_sticker);
        setFileSticker(file_sticker);
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(int isSeen) {
        this.isSeen = isSeen;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFolderSticker() {
        return folder_sticker;
    }

    public void setFolderSticker(String folder_sticker) {
        this.folder_sticker = folder_sticker;
    }

    public String getFileSticker() {
        return file_sticker;
    }

    public void setFileSticker(String file_sticker) {
        this.file_sticker = file_sticker;
    }
}
