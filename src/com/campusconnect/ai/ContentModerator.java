package com.campusconnect.ai;

import java.util.*;
import java.util.regex.Pattern;

 
public class ContentModerator {

    
    private static final Set<String> PROFANITY_WORDS = new HashSet<>(Arrays.asList(
            "spam", "scam", "fake", "fraud" 
    ));

    
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern REPEATED_CHARS = Pattern.compile("(.)\\1{4,}");

     
    public static String checkContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty";
        }

        if (content.length() > 5000) {
            return "Content exceeds maximum length (5000 characters)";
        }

        
        if (isSpam(content)) {
            return "Content detected as potential spam";
        }

        
        if (containsProfanity(content)) {
            return "Content contains inappropriate language";
        }

        return null; 
    }

     
    public static boolean isSpam(String content) {
        
        long urlCount = countMatches(content, URL_PATTERN);
        if (urlCount > 3)
            return true;

        
        long emailCount = countMatches(content, EMAIL_PATTERN);
        if (emailCount > 2)
            return true;

        
        if (REPEATED_CHARS.matcher(content).find())
            return true;

        
        long uppercaseCount = content.chars().filter(Character::isUpperCase).count();
        long letterCount = content.chars().filter(Character::isLetter).count();
        if (letterCount > 20 && uppercaseCount > letterCount * 0.7)
            return true;

        
        long specialCount = content.chars()
                .filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c))
                .count();
        if (content.length() > 10 && specialCount > content.length() * 0.5)
            return true;

        return false;
    }

     
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

     
    public static String filterProfanity(String content) {
        String result = content;
        for (String word : PROFANITY_WORDS) {
            String replacement = "*".repeat(word.length());
            result = result.replaceAll("(?i)\\b" + word + "\\b", replacement);
        }
        return result;
    }

     
    public static double scoreContent(String content) {
        if (content == null || content.trim().isEmpty())
            return 0.0;

        double score = 1.0;

        
        if (isSpam(content))
            score -= 0.5;

        
        if (containsProfanity(content))
            score -= 0.3;

        
        if (content.length() < 10)
            score -= 0.2;

        
        if (content.length() >= 50 && content.length() <= 500)
            score += 0.1;

        
        if (content.matches(".*[.!?]\\s*$"))
            score += 0.1;

        return Math.max(0.0, Math.min(1.0, score));
    }

     
    private static long countMatches(String text, Pattern pattern) {
        return pattern.matcher(text).results().count();
    }
}
