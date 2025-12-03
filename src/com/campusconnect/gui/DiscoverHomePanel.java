package com.campusconnect.gui;

import com.campusconnect.ai.TrendingAnalyzer;
import com.campusconnect.core.*;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DiscoverHomePanel extends ModernPanel {
    private JPanel statsPanel;
    private JPanel trendingPanel;

    public DiscoverHomePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("✨ Discover");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);

        // Main content with grid
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        mainPanel.setOpaque(false);

        // Top Row: Stats Cards
        statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setOpaque(false);
        mainPanel.add(statsPanel);

        // Bottom Row: Trending Content
        trendingPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        trendingPanel.setOpaque(false);
        mainPanel.add(trendingPanel);

        add(mainPanel, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        statsPanel.removeAll();
        trendingPanel.removeAll();

        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        // Stats Cards
        statsPanel.add(
                createStatCard("👥", "Followers", String.valueOf(currentUser.getFollowerCount()), ModernTheme.ACCENT));
        statsPanel.add(createStatCard("📝", "Posts",
                String.valueOf(DataManager.getInstance().getPostsByUser(currentUser.getId()).size()),
                new Color(100, 200, 255)));
        statsPanel.add(createStatCard("🔔", "Alerts",
                String.valueOf(DataManager.getInstance().getUnreadNotificationCount(currentUser.getId())),
                new Color(255, 150, 100)));
        statsPanel.add(createStatCard("🌐", "Network", String.valueOf(currentUser.getFollowingCount()),
                new Color(150, 255, 150)));

        // Trending Hashtags
        JPanel hashtagPanel = createTrendingHashtagsPanel();
        trendingPanel.add(hashtagPanel);

        // Active Users
        JPanel activeUsersPanel = createActiveUsersPanel();
        trendingPanel.add(activeUsersPanel);

        statsPanel.revalidate();
        statsPanel.repaint();
        trendingPanel.revalidate();
        trendingPanel.repaint();
    }

    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(15, 15, 15, 15)));
        card.setPreferredSize(new Dimension(150, 100));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(valueLabel);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(ModernTheme.FONT_SMALL);
        labelLabel.setForeground(ModernTheme.TEXT_DARK);
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(labelLabel);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTrendingHashtagsPanel() {
        JPanel panel = new ModernPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("🔥 Trending Hashtags");
        title.setFont(ModernTheme.FONT_BOLD);
        title.setForeground(ModernTheme.TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        List<String> trending = TrendingAnalyzer.getTrendingHashtags(
                DataManager.getInstance().getAllPosts(), 5);

        if (trending.isEmpty()) {
            JLabel empty = new JLabel("No trending hashtags yet");
            empty.setForeground(ModernTheme.TEXT_DARK);
            content.add(empty);
        } else {
            int rank = 1;
            for (String tag : trending) {
                JLabel hashtagLabel = new JLabel(rank + ". #" + tag);
                hashtagLabel.setFont(ModernTheme.FONT_REGULAR);
                hashtagLabel.setForeground(ModernTheme.TEXT_LIGHT);
                content.add(hashtagLabel);
                content.add(Box.createVerticalStrut(5));
                rank++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActiveUsersPanel() {
        JPanel panel = new ModernPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("⭐ Most Active Users");
        title.setFont(ModernTheme.FONT_BOLD);
        title.setForeground(ModernTheme.TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        List<User> activeUsers = TrendingAnalyzer.getActiveUsers(
                DataManager.getInstance().getUsers(),
                DataManager.getInstance().getAllPosts(),
                5);

        if (activeUsers.isEmpty()) {
            JLabel empty = new JLabel("No activity data yet");
            empty.setForeground(ModernTheme.TEXT_DARK);
            content.add(empty);
        } else {
            int rank = 1;
            for (User user : activeUsers) {
                JLabel userLabel = new JLabel(rank + ". " + user.getName() + " (" + user.getUserType() + ")");
                userLabel.setFont(ModernTheme.FONT_REGULAR);
                userLabel.setForeground(ModernTheme.TEXT_LIGHT);
                content.add(userLabel);
                content.add(Box.createVerticalStrut(5));
                rank++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
