package by.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int senderId;
    private int toUser;
    private String content;
    private String date = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss").format(new Date());

    public Message() {
    }

    public Message(int senderId, int toUser, String content) {
        this.content = content;
        this.toUser = toUser;
        this.senderId = senderId;
        this.date = getDate();
    }
    public Message(int senderId, int toUser, String content, String date) {
        this.content = content;
        this.toUser = toUser;
        this.senderId = senderId;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", toUser=" + toUser +
                ", sendUser=" + senderId +
                ", date='" + date + '\'' +
                '}';
    }
}
