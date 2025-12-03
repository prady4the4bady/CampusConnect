package com.campusconnect.ai;

import com.campusconnect.core.User;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SmartSearch provides fuzzy search and auto-complete functionality
 */
public class SmartSearch {

    /**
     * Search users with ranking by relevance
     */
    public static List<User> searchUsers(String query, List<User> users, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase().trim();
        Map<User, Double> relevanceScores = new HashMap<>();

        for (User user : users) {
            double score = calculateRelevanceScore(lowerQuery, user);
            if (score > 0) {
                relevanceScores.put(user, score);
            }
        }

        return relevanceScores.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for user search
     */
    private static double calculateRelevanceScore(String query, User user) {
        double score = 0.0;

        String name = user.getName().toLowerCase();
        String email = user.getEmail().toLowerCase();

        // Exact match (highest score)
        if (name.equals(query)) {
            score += 100.0;
        }
        // Starts with query
        else if (name.startsWith(query)) {
            score += 50.0;
        }
        // Contains query
        else if (name.contains(query)) {
            score += 25.0;
        }
        // Fuzzy match
        else {
            double fuzzyScore = calculateLevenshteinSimilarity(query, name);
            if (fuzzyScore > 0.7) {
                score += fuzzyScore * 20.0;
            }
        }

        // Email match
        if (email.contains(query)) {
            score += 15.0;
        }

        // Popularity boost (users with more followers rank slightly higher)
        score += Math.log10(user.getFollowerCount() + 1);

        return score;
    }

    /**
     * Get search suggestions for auto-complete
     */
    public static List<String> getSuggestions(String query, List<User> users, int limit) {
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase();
        Set<String> suggestions = new LinkedHashSet<>();

        for (User user : users) {
            String name = user.getName();
            if (name.toLowerCase().startsWith(lowerQuery)) {
                suggestions.add(name);
            }
        }

        return suggestions.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Calculate Levenshtein distance-based similarity
     */
    private static double calculateLevenshteinSimilarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0)
            return 1.0;
        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Extract @mentions from text
     */
    public static List<String> extractMentions(String text) {
        List<String> mentions = new ArrayList<>();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (word.startsWith("@") && word.length() > 1) {
                mentions.add(word.substring(1).replaceAll("[^a-zA-Z0-9_]", ""));
            }
        }

        return mentions;
    }
}
