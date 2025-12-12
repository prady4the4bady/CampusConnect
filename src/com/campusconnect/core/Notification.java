package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification implements Storable, Comparable<Notification> {
    private String id;
    private String userId; 
    private String type; 
    private String message;
    private String relatedId; 
    private LocalDateTime timestamp;
    private boolean read;
    private int priority; 

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Notification(String id, String userId, String type, String message, String relatedId) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.timestamp = LocalDateTime.now();
        this.read = false;
        this.priority = calculatePriority(type);
    }

    private int calculatePriority(String type) {
        switch (type) {
            case "MENTION":
                return 5;
            case "COMMENT":
                return 4;
            case "FOLLOW":
                return 3;
            case "LIKE":
                return 2;
            default:
                return 1;
        }
    }

    
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public int getPriority() {
        return priority;
    }

    
    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toCSV() {
        String safeMessage = message.replace(",", ";");
        return id + "," + userId + "," + type + "," + safeMessage + "," + relatedId + "," +
                timestamp.format(formatter) + "," + read + "," + priority;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 8) {
            this.id = parts[0];
            this.userId = parts[1];
            this.type = parts[2];
            this.message = parts[3].replace(";", ",");
            this.relatedId = parts[4];
            this.timestamp = LocalDateTime.parse(parts[5], formatter);
            this.read = Boolean.parseBoolean(parts[6]);
            this.priority = Integer.parseInt(parts[7]);
        }
    }

    @Override
    public int compareTo(Notification other) {
        
        if (this.priority != other.priority) {
            return other.priority - this.priority;
        }
        return other.timestamp.compareTo(this.timestamp);
    }
}
