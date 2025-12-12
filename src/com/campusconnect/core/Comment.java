package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Comment implements Storable, Comparable<Comment> {
    private String id;
    private String postId;
    private String authorId;
    private String content;
    private LocalDateTime timestamp;
    private Set<String> likes;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Comment(String id, String postId, String authorId, String content) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.likes = new HashSet<>();
    }

    
    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public int getLikeCount() {
        return likes.size();
    }

    
    public void addLike(String userId) {
        likes.add(userId);
    }

    public void removeLike(String userId) {
        likes.remove(userId);
    }

    public boolean isLikedBy(String userId) {
        return likes.contains(userId);
    }

    @Override
    public String toCSV() {
        String safeContent = content.replace(",", ";").replace("\n", "\\n");
        String likeStr = String.join("|", likes);
        return id + "," + postId + "," + authorId + "," + safeContent + "," +
                timestamp.format(formatter) + "," + likeStr;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 6) {
            this.id = parts[0];
            this.postId = parts[1];
            this.authorId = parts[2];
            this.content = parts[3].replace(";", ",").replace("\\n", "\n");
            this.timestamp = LocalDateTime.parse(parts[4], formatter);

            if (!parts[5].isEmpty()) {
                this.likes = new HashSet<>(Arrays.asList(parts[5].split("\\|")));
            }
        }
    }

    @Override
    public int compareTo(Comment other) {
        
        return this.timestamp.compareTo(other.timestamp);
    }
}
