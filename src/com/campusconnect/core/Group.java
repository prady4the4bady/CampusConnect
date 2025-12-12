package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group implements Storable {
    private String id;
    private String name;
    private String description;
    private List<String> memberIds;
    private Set<String> councilMembers;
    private String facultyIncharge;

    public Group(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.memberIds = new ArrayList<>();
        this.councilMembers = new HashSet<>();
        this.facultyIncharge = "";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public Set<String> getCouncilMembers() {
        return councilMembers;
    }

    public String getFacultyIncharge() {
        return facultyIncharge;
    }

    public void setFacultyIncharge(String userId) {
        this.facultyIncharge = userId;
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    public void removeMember(String userId) {
        memberIds.remove(userId);
    }

    
    public void addCouncilMember(String userId) {
        councilMembers.add(userId);
    }

    public void removeCouncilMember(String userId) {
        councilMembers.remove(userId);
    }

    public boolean isCouncil(String userId) {
        return councilMembers.contains(userId);
    }

    public boolean isFacultyIncharge(String userId) {
        return facultyIncharge != null && facultyIncharge.equals(userId);
    }

    @Override
    public String toCSV() {
        return id + "," + name + "," + description + "," + String.join("|", memberIds) + "," +
                String.join("|", councilMembers) + "," + facultyIncharge;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 3) {
            this.id = parts[0];
            this.name = parts[1];
            this.description = parts[2];
            if (parts.length > 3 && !parts[3].isEmpty()) {
                String[] members = parts[3].split("\\|");
                for (String m : members) {
                    memberIds.add(m);
                }
            }
            if (parts.length > 4 && !parts[4].isEmpty()) {
                String[] council = parts[4].split("\\|");
                for (String c : council) {
                    councilMembers.add(c);
                }
            }
            if (parts.length > 5) {
                this.facultyIncharge = parts[5];
            }
        }
    }
}
