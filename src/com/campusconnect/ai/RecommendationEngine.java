package com.campusconnect.ai;

import com.campusconnect.core.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RecommendationEngine provides intelligent user and post recommendations
 * using lightweight similarity algorithms
 */
public class RecommendationEngine {

    /**
     * Recommend users to follow based on mutual interests and connections
     */
    public static List<User> getUserRecommendations(User currentUser, List<User> allUsers, int limit) {
        if (currentUser == null)
            return new ArrayList<>();

        Map<User, Double> scores = new HashMap<>();

        for (User user : allUsers) {
            if (user.getId().equals(currentUser.getId()))
                continue;
            if (currentUser.isFollowing(user.getId()))
                continue;
            if (currentUser.isBlocked(user.getId()))
                continue;

            double score = calculateUserSimilarity(currentUser, user, allUsers);
            scores.put(user, score);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate similarity between two users
     */
    private static double calculateUserSimilarity(User user1, User user2, List<User> allUsers) {
        double score = 0.0;

        // Interest similarity (50% weight)
        double interestScore = calculateInterestSimilarity(user1, user2);
        score += interestScore * 0.5;

        // Mutual connections (30% weight)
        double mutualScore = calculateMutualConnections(user1, user2);
        score += mutualScore * 0.3;

        // Popularity factor (20% weight) - users with more followers are slightly
        // boosted
        double popularityScore = Math.min(user2.getFollowerCount() / 100.0, 1.0);
        score += popularityScore * 0.2;

        return score;
    }

    /**
     * Calculate interest overlap between users
     */
    private static double calculateInterestSimilarity(User user1, User user2) {
        Set<String> interests1 = user1.getInterests().stream()
                .map(i -> i.getName().toLowerCase())
                .collect(Collectors.toSet());
        Set<String> interests2 = user2.getInterests().stream()
                .map(i -> i.getName().toLowerCase())
                .collect(Collectors.toSet());

        if (interests1.isEmpty() || interests2.isEmpty())
            return 0.0;

        Set<String> intersection = new HashSet<>(interests1);
        intersection.retainAll(interests2);

        Set<String> union = new HashSet<>(interests1);
        union.addAll(interests2);

        return (double) intersection.size() / union.size(); // Jaccard similarity
    }

    /**
     * Calculate mutual connections score
     */
    private static double calculateMutualConnections(User user1, User user2) {
        Set<String> following1 = user1.getFollowing();
        Set<String> following2 = user2.getFollowing();

        if (following1.isEmpty() && following2.isEmpty())
            return 0.0;

        Set<String> mutual = new HashSet<>(following1);
        mutual.retainAll(following2);

        return Math.min((double) mutual.size() / 10.0, 1.0); // Max score at 10 mutual
    }

    /**
     * Recommend posts for user's feed based on their interests and connections
     */
    public static List<Post> getPostRecommendations(User currentUser, List<Post> allPosts,
            List<User> allUsers, int limit) {
        if (currentUser == null)
            return new ArrayList<>();

        Map<Post, Double> scores = new HashMap<>();

        for (Post post : allPosts) {
            // Skip posts from blocked users
            User author = findUserById(post.getAuthorId(), allUsers);
            if (author != null && currentUser.isBlocked(author.getId()))
                continue;

            double score = calculatePostRelevance(currentUser, post, author, allUsers);
            scores.put(post, score);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate how relevant a post is to a user
     */
    private static double calculatePostRelevance(User user, Post post, User author, List<User> allUsers) {
        double score = 0.0;

        // From followed users (highest weight)
        if (user.isFollowing(post.getAuthorId())) {
            score += 1.0;
        }

        // Interest match
        if (author != null) {
            double interestScore = calculateInterestSimilarity(user, author);
            score += interestScore * 0.5;
        }

        // Engagement score
        double engagementScore = calculateEngagementScore(post);
        score += engagementScore * 0.3;

        // Recency bonus - newer posts get a boost
        long ageHours = java.time.Duration.between(post.getTimestamp(),
                java.time.LocalDateTime.now()).toHours();
        double recencyScore = Math.max(0, 1.0 - (ageHours / 48.0)); // Decay over 48 hours
        score += recencyScore * 0.2;

        return score;
    }

    /**
     * Calculate engagement score for a post
     */
    private static double calculateEngagementScore(Post post) {
        int likes = post.getLikeCount();
        int comments = post.getCommentCount();

        // Weighted average: comments are worth more than likes
        double score = (likes * 1.0 + comments * 3.0) / 10.0;
        return Math.min(score, 1.0); // Cap at 1.0
    }

    /**
     * Find user by ID
     */
    private static User findUserById(String userId, List<User> users) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
