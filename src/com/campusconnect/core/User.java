package com.campusconnect.core;

import com.campusconnect.interfaces.Searchable;
import com.campusconnect.interfaces.Storable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class User implements Searchable, Storable {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected List<Interest> interests;
    protected String profilePicUrl;
    protected String bio;
    protected java.util.Set<String> followers; 
    protected java.util.Set<String> following; 
    protected java.util.Set<String> blocked; 

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.interests = new ArrayList<>();
        this.profilePicUrl = "";
        this.bio = "";
        this.followers = new java.util.HashSet<>();
        this.following = new java.util.HashSet<>();
        this.blocked = new java.util.HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getBio() {
        return bio;
    }

    public java.util.Set<String> getFollowers() {
        return followers;
    }

    public java.util.Set<String> getFollowing() {
        return following;
    }

    public int getFollowerCount() {
        return followers.size();
    }

    public int getFollowingCount() {
        return following.size();
    }

    public void setProfilePicUrl(String url) {
        this.profilePicUrl = url;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void follow(String userId) {
        if (!blocked.contains(userId)) {
            following.add(userId);
        }
    }

    public void unfollow(String userId) {
        following.remove(userId);
    }

    public void addFollower(String userId) {
        followers.add(userId);
    }

    public void removeFollower(String userId) {
        followers.remove(userId);
    }

    public boolean isFollowing(String userId) {
        return following.contains(userId);
    }

    public void block(String userId) {
        blocked.add(userId);
        following.remove(userId);
        followers.remove(userId);
    }

    public void unblock(String userId) {
        blocked.remove(userId);
    }

    public boolean isBlocked(String userId) {
        return blocked.contains(userId);
    }

    public void addInterest(Interest interest) {
        if (!interests.contains(interest)) {
            interests.add(interest);
        }
    }

    public void removeInterest(Interest interest) {
        interests.remove(interest);
    }

    @Override
    public boolean matches(String keyword) {
        if (name.toLowerCase().contains(keyword.toLowerCase()))
            return true;
        for (Interest i : interests) {
            if (i.getName().toLowerCase().contains(keyword.toLowerCase()))
                return true;
        }
        return false;
    }

    
    public abstract String getUserType();

    @Override
    public String toCSV() {
        
        
        String interestString = interests.stream()
                .map(i -> i.getName() + ":" + i.getCategory())
                .collect(Collectors.joining("|"));
        String safeBio = bio.replace(",", ";");
        String followerStr = String.join("|", followers);
        String followingStr = String.join("|", following);
        String blockedStr = String.join("|", blocked);
        return String.join(",", getUserType(), id, name, email, password, interestString,
                safeBio, profilePicUrl, followerStr, followingStr, blockedStr);
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 5) {
            this.id = parts[1];
            this.name = parts[2];
            this.email = parts[3];
            this.password = parts[4];
            if (parts.length > 5 && !parts[5].isEmpty()) {
                String[] interestParts = parts[5].split("\\|");
                for (String ip : interestParts) {
                    String[] iDetails = ip.split(":");
                    if (iDetails.length == 2) {
                        addInterest(new Interest(iDetails[0], iDetails[1]));
                    }
                }
            }
            if (parts.length > 6) {
                this.bio = parts[6].replace(";", ",");
            }
            if (parts.length > 7) {
                this.profilePicUrl = parts[7];
            }
            if (parts.length > 8 && !parts[8].isEmpty()) {
                this.followers = new java.util.HashSet<>(java.util.Arrays.asList(parts[8].split("\\|")));
            }
            if (parts.length > 9 && !parts[9].isEmpty()) {
                this.following = new java.util.HashSet<>(java.util.Arrays.asList(parts[9].split("\\|")));
            }
            if (parts.length > 10 && !parts[10].isEmpty()) {
                this.blocked = new java.util.HashSet<>(java.util.Arrays.asList(parts[10].split("\\|")));
            }
        }
    }
}
