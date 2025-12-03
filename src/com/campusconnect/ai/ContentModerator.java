package com.campusconnect.ai;

import java.util.*;
import java.util.regex.Pattern;

/**
 * ContentModerator provides lightweight content filtering and spam detection
 */
public class ContentModerator {

    // Profanity word list (basic set - can be expanded)
    private static final Set<String> PROFANITY_WORDS = new HashSet<>(Arrays.asList(
            "spam", "scam", "fake", "fraud" // Add more as needed
    ));

    // Suspicious patterns
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern REPEATED_CHARS = Pattern.compile("(.)\\1{4,}");

    /**
     * Check if content is appropriate (returns error message or null if OK)
     */
    public static String checkContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty";
        }

        if (content.length() > 5000) {
            return "Content exceeds maximum length (5000 characters)";
        }

        // Check for spam patterns
        if (isSpam(content)) {
            return "Content detected as potential spam";
        }

        // Check for profanity
        if (containsProfanity(content)) {
            return "Content contains inappropriate language";
        }

        return null; // Content is OK
    }

    /**
     * Detect spam content
     */
    public static boolean isSpam(String content) {
        // Too many URLs
        long urlCount = countMatches(content, URL_PATTERN);
        if (urlCount > 3)
            return true;

        // Too many emails
        long emailCount = countMatches(content, EMAIL_PATTERN);
        if (emailCount > 2)
            return true;

        // Repeated characters (like "aaaaa")
        if (REPEATED_CHARS.matcher(content).find())
            return true;

        // All caps (more than 70% uppercase)
        long uppercaseCount = content.chars().filter(Character::isUpperCase).count();
        long letterCount = content.chars().filter(Character::isLetter).count();
        if (letterCount > 20 && uppercaseCount > letterCount * 0.7)
            return true;

        // Excessive special characters
        long specialCount = content.chars()
                .filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c))
                .count();
        if (content.length() > 10 && specialCount > content.length() * 0.5)
            return true;

        return false;
    }

    /**
     * Check for profanity
     */
    public static boolean containsProfanity(String content) {
        String lower = content.toLowerCase();
        String[] words = lower.split("\\s+");

        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-z]", "");
            if (PROFANITY_WORDS.contains(cleanWord)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filter profanity from content
     */
    public static String filterProfanity(String content) {
        String result = content;
        for (String word : PROFANITY_WORDS) {
            String replacement = "*".repeat(word.length());
            result = result.replaceAll("(?i)\\b" + word + "\\b", replacement);
        }
        return result;
    }

    /**
     * Score content quality (0.0 to 1.0)
     */
    public static double scoreContent(String content) {
        if (content == null || content.trim().isEmpty())
            return 0.0;

        double score = 1.0;

        // Penalize spam
        if (isSpam(content))
            score -= 0.5;

        // Penalize profanity
        if (containsProfanity(content))
            score -= 0.3;

        // Penalize very short content
        if (content.length() < 10)
            score -= 0.2;

        // Bonus for moderate length
        if (content.length() >= 50 && content.length() <= 500)
            score += 0.1;

        // Bonus for complete sentences
        if (content.matches(".*[.!?]\\s*$"))
            score += 0.1;

        return Math.max(0.0, Math.min(1.0, score));
    }

    /**
     * Count pattern matches
     */
    private static long countMatches(String text, Pattern pattern) {
        return pattern.matcher(text).results().count();
    }
}
