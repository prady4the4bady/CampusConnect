package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.util.ArrayList;
import java.util.List;

public class WalkieTalkieChannel implements Storable {
    private String id;
    private String name;
    private boolean isPrivate;
    private String inviteCode;
    private List<String> activeUserIds;

    public WalkieTalkieChannel(String id, String name, boolean isPrivate, String inviteCode) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.inviteCode = inviteCode;
        this.activeUserIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public List<String> getActiveUserIds() {
        return activeUserIds;
    }

    public void join(String userId) {
        if (!activeUserIds.contains(userId)) {
            activeUserIds.add(userId);
        }
    }

    public void leave(String userId) {
        activeUserIds.remove(userId);
    }

    @Override
    public String toCSV() {
        return id + "," + name + "," + isPrivate + "," + inviteCode;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 4) {
            this.id = parts[0];
            this.name = parts[1];
            this.isPrivate = Boolean.parseBoolean(parts[2]);
            this.inviteCode = parts[3];
        }
    }
}
