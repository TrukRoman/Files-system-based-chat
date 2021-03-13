package by.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String content;
    private int toUser;
    private int senderId;
    private String date;

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
        return new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss").format(new Date());
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
