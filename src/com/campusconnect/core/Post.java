package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Post implements Storable, Comparable<Post> {
    private String id;
    private String authorId;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime timestamp;
    private Set<String> likes; 
    private List<Comment> comments;
    private String privacy; 

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Post(String id, String authorId, String content) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.imageUrls = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
        this.likes = new HashSet<>();
        this.comments = new ArrayList<>();
        this.privacy = "PUBLIC";
    }

    
    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getPrivacy() {
        return privacy;
    }

    public int getLikeCount() {
        return likes.size();
    }

    public int getCommentCount() {
        return comments.size();
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void addImageUrl(String url) {
        this.imageUrls.add(url);
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

    
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(String commentId) {
        comments.removeIf(c -> c.getId().equals(commentId));
    }

    @Override
    public String toCSV() {
        
        String safeContent = content.replace(",", ";").replace("\n", "\\n");
        String imageStr = String.join("|", imageUrls);
        String likeStr = String.join("|", likes);
        return id + "," + authorId + "," + safeContent + "," + imageStr + "," +
                timestamp.format(formatter) + "," + likeStr + "," + privacy;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 7) {
            this.id = parts[0];
            this.authorId = parts[1];
            this.content = parts[2].replace(";", ",").replace("\\n", "\n");

            if (!parts[3].isEmpty()) {
                this.imageUrls = new ArrayList<>(Arrays.asList(parts[3].split("\\|")));
            }

            this.timestamp = LocalDateTime.parse(parts[4], formatter);

            if (!parts[5].isEmpty()) {
                this.likes = new HashSet<>(Arrays.asList(parts[5].split("\\|")));
            }

            this.privacy = parts[6];
        }
    }

    @Override
    public int compareTo(Post other) {
        
        return other.timestamp.compareTo(this.timestamp);
    }
}
