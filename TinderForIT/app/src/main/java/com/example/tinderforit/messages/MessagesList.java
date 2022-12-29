package com.example.tinderforit.messages;

public class MessagesList {
    String userChatKey, userUID, userName, userLastMessage, userProfileURL;
    int seenMessage;

    public MessagesList(String userChatKey,String userUID, String userName, String userLastMessage, String userProfileURL, int seenMessage) {
        this.userChatKey=userChatKey;
        this.userUID = userUID;
        this.userName = userName;
        this.userLastMessage = userLastMessage;
        this.userProfileURL = userProfileURL;
        this.seenMessage = seenMessage;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getUserChatKey() {
        return userChatKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLastMessage() {
        return userLastMessage;
    }

    public String getUserProfileURL() {
        return userProfileURL;
    }

    public int getSeenMessage() {
        return seenMessage;
    }
}
