package com.campusconnect.ai;

import com.campusconnect.core.Post;
import com.campusconnect.core.User;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

 
public class TrendingAnalyzer {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

     
    public static List<String> getTrendingHashtags(List<Post> posts, int limit) {
        Map<String, Integer> hashtagCounts = new HashMap<>();

        
        java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusHours(24);

        for (Post post : posts) {
            if (post.getTimestamp().isAfter(cutoff)) {
                List<String> hashtags = extractHashtags(post.getContent());
                for (String tag : hashtags) {
                    hashtagCounts.put(tag, hashtagCounts.getOrDefault(tag, 0) + 1);
                }
            }
        }

        return hashtagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

     
    public static List<String> extractHashtags(String text) {
        List<String> hashtags = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        while (matcher.find()) {
            hashtags.add(matcher.group(1).toLowerCase());
        }
        return hashtags;
    }

     
    public static List<Post> getTrendingPosts(List<Post> posts, int limit) {
        
        Map<Post, Double> trendingScores = new HashMap<>();

        for (Post post : posts) {
            double score = calculateTrendingScore(post);
            trendingScores.put(post, score);
        }

        return trendingScores.entrySet().stream()
                .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

     
    private static double calculateTrendingScore(Post post) {
        double score = 0.0;

        
        int likes = post.getLikeCount();
        int comments = post.getCommentCount();

        
        double engagementScore = likes * 1.0 + comments * 3.0;
        score += engagementScore;

        
        long ageHours = java.time.Duration.between(
                post.getTimestamp(),
                java.time.LocalDateTime.now()).toHours();

        
        double recencyMultiplier = Math.exp(-ageHours / 12.0); 
        score *= recencyMultiplier;

        
        if (ageHours > 0) {
            double velocity = engagementScore / Math.max(ageHours, 1);
            score += velocity * 5.0;
        }

        return score;
    }

     
    public static List<User> getActiveUsers(List<User> users, List<Post> posts, int limit) {
        Map<String, Integer> activityScores = new HashMap<>();

        
        java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusDays(7);
        for (Post post : posts) {
            if (post.getTimestamp().isAfter(cutoff)) {
                activityScores.put(post.getAuthorId(),
                        activityScores.getOrDefault(post.getAuthorId(), 0) + 10);
            }
        }

        return users.stream()
                .sorted((u1, u2) -> {
                    int score1 = activityScores.getOrDefault(u1.getId(), 0);
                    int score2 = activityScores.getOrDefault(u2.getId(), 0);
                    return Integer.compare(score2, score1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

     
    public static double calculateEngagementRate(String userId, List<Post> posts) {
        List<Post> userPosts = posts.stream()
                .filter(p -> p.getAuthorId().equals(userId))
                .collect(Collectors.toList());

        if (userPosts.isEmpty())
            return 0.0;

        int totalLikes = userPosts.stream().mapToInt(Post::getLikeCount).sum();
        int totalComments = userPosts.stream().mapToInt(Post::getCommentCount).sum();

        return (double) (totalLikes + totalComments * 2) / userPosts.size();
    }
}
