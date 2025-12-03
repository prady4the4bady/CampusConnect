package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GroupRequest implements Storable, Comparable<GroupRequest> {
    private String id;
    private String groupId;
    private String userId;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime timestamp;

    public GroupRequest(String id, String groupId, String userId) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Status management
    public void approve() {
        this.status = "APPROVED";
    }

    public void reject() {
        this.status = "REJECTED";
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    @Override
    public String toCSV() {
        return id + "," + groupId + "," + userId + "," + status + "," +
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 5) {
            this.id = parts[0];
            this.groupId = parts[1];
            this.userId = parts[2];
            this.status = parts[3];
            this.timestamp = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    @Override
    public int compareTo(GroupRequest other) {
        return other.timestamp.compareTo(this.timestamp); // Newest first
    }
}
