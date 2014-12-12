// IMyAidlInterface.aidl
package com.relhs.asianfinder;

// Declare any non-default types here with import statements

interface IAFPushService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendChatMessage(int userId, String message, int localId);

    void sendSticker(String folder, String file, int userId, int localId);

    void getChatMessage(String jsonData);

    void initializeChatting(int userId);

    void initializeChattingOk(int threadId);


}
