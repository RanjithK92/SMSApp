package com.activities.sms;

import java.util.ArrayList;

public class SenderModel {
    String sender;
    ArrayList<SMSMessage> messages;

    public String getSender() {
        return sender == null ? "" : sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ArrayList<SMSMessage> getMessages() {
        return messages == null ? messages = new ArrayList<>() : messages;
    }

    public void setMessages(ArrayList<SMSMessage> messages) {
        this.messages = messages;
    }
}
