package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Storable {
    private String senderId;
    private String content;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Message(String senderId, String content) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    
    public Message(String senderId, String content, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toCSV() {
        return senderId + "," + content.replace(",", ";") + "," + timestamp.format(formatter);
    }

    @Override
    public void fromCSV(String csv) {
        
    }
    
    public static Message parse(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 3) {
            return new Message(parts[0], parts[1], LocalDateTime.parse(parts[2], formatter));
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "[" + timestamp.format(DateTimeFormatter.ofPattern("HH:mm")) + "] " + senderId + ": " + content;
    }
}
