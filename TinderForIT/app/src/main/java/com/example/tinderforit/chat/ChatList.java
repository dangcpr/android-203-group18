package com.example.tinderforit.chat;

public class ChatList {
    private String FromUID, Msg, DateSend, TimeSend;

    public ChatList(String fromUID, String msg, String dateSend, String timeSend) {
        FromUID = fromUID;
        Msg = msg;
        DateSend = dateSend;
        TimeSend = timeSend;
    }

    public String getFromUID() {
        return FromUID;
    }

    public String getMsg() {
        return Msg;
    }

    public String getDateSend() {
        return DateSend;
    }

    public String getTimeSend() {
        return TimeSend;
    }
}
