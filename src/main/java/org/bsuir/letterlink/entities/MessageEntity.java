package org.bsuir.letterlink.entities;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeBodyPart;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageEntity {
    private Message message;
    private String subject;
    private String sender;
    private String recipient;
    private Date date;
    private boolean isRead;
    //    private List<MimeBodyPart> attachmentList = new ArrayList<>();
//    private boolean hasAttachments = false;


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public MessageEntity(Message message, String subject, String sender, Date date, boolean isRead) {
        this.message = message;
        this.subject = subject;
        this.sender = sender;
//        this.recipient = recipient;
        this.date = date;
        this.isRead = isRead;
    }
}
