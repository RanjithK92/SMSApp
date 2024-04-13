package com.activities.sms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SMSMessage {
    String message;
    String sender;
    long date;
    boolean read;
    int type;
    int thread;
    String service;

    public SMSMessage(String message, String sender, long date, boolean read, int type, int thread,
                      String service) {
        this.message = message;
        this.sender = sender;
        this.date = date;
        this.read = read;
        this.type = type;
        this.thread = thread;
        this.service = service;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender == null ? "" : sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getDate() {
        return date;
    }

    public String getDateTimeString() {
        Date d = new Date(date);
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault());
        return format.format(date);
    }

    public String getDateString() {
        Date d = new Date(date);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return format.format(date);
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public String getService() {
        return service == null ? "" : service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
