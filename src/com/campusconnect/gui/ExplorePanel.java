package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.ai.SmartSearch;
import com.campusconnect.ai.RecommendationEngine;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ExplorePanel extends ModernPanel {
    private JPanel usersContainer;
    private ModernTextField searchField;
    private JLabel resultCountLabel;

    public ExplorePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        
        usersContainer = new JPanel();
        usersContainer.setLayout(new BoxLayout(usersContainer, BoxLayout.Y_AXIS));
        usersContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(usersContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JLabel titleLabel = new JLabel("Discover People");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);

        
        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);

        searchField = new ModernTextField(30);
        searchBarPanel.add(searchField, BorderLayout.CENTER);

        ModernButton searchButton = new ModernButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchBarPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchBarPanel, BorderLayout.CENTER);

        
        resultCountLabel = new JLabel("Showing all users");
        resultCountLabel.setFont(ModernTheme.FONT_SMALL);
        resultCountLabel.setForeground(ModernTheme.TEXT_DARK);
        panel.add(resultCountLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        usersContainer.removeAll();

        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        List<User> allUsers = DataManager.getInstance().getUsers();
        List<User> results;

        if (query.isEmpty()) {
            
            results = allUsers.stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .collect(java.util.stream.Collectors.toList());
            resultCountLabel.setText("Showing all users (" + results.size() + ")");
        } else {
            
            results = SmartSearch.searchUsers(query, allUsers, 50);
            
            results.removeIf(u -> u.getId().equals(currentUser.getId()));
            resultCountLabel.setText("Found " + results.size() + " users matching \"" + query + "\"");
        }

        if (results.isEmpty()) {
            JLabel emptyLabel = new JLabel("No users found");
            emptyLabel.setFont(ModernTheme.FONT_REGULAR);
            emptyLabel.setForeground(ModernTheme.TEXT_DARK);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            usersContainer.add(Box.createVerticalStrut(50));
            usersContainer.add(emptyLabel);
        } else {
            for (User user : results) {
                JPanel userCard = createUserCard(user);
                usersContainer.add(userCard);
                usersContainer.add(Box.createVerticalStrut(10));
            }
        }

        usersContainer.revalidate();
        usersContainer.repaint();
    }

    public void refresh() {
        searchField.setText("");
        performSearch(); 

        
        showRecommendations();
    }

    private void showRecommendations() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        List<User> allUsers = DataManager.getInstance().getUsers();
        List<User> recommended = RecommendationEngine.getUserRecommendations(currentUser, allUsers, 3);

        if (!recommended.isEmpty()) {
            usersContainer.removeAll();

            
            JLabel recLabel = new JLabel("🌟 Recommended for You");
            recLabel.setFont(ModernTheme.FONT_BOLD);
            recLabel.setForeground(ModernTheme.ACCENT);
            recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            usersContainer.add(recLabel);
            usersContainer.add(Box.createVerticalStrut(10));

            for (User user : recommended) {
                JPanel userCard = createUserCard(user);
                usersContainer.add(userCard);
                usersContainer.add(Box.createVerticalStrut(10));
            }

            
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            usersContainer.add(separator);
            usersContainer.add(Box.createVerticalStrut(15));

            
            JLabel allLabel = new JLabel("All Users");
            allLabel.setFont(ModernTheme.FONT_BOLD);
            allLabel.setForeground(ModernTheme.TEXT_LIGHT);
            allLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            usersContainer.add(allLabel);
            usersContainer.add(Box.createVerticalStrut(10));

            
            List<User> otherUsers = allUsers.stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()) && !recommended.contains(u))
                    .collect(java.util.stream.Collectors.toList());

            for (User user : otherUsers) {
                JPanel userCard = createUserCard(user);
                usersContainer.add(userCard);
                usersContainer.add(Box.createVerticalStrut(10));
            }

            resultCountLabel.setText("Showing " + (recommended.size() + otherUsers.size()) + " users");
        } else {
            performSearch(); 
        }

        usersContainer.revalidate();
        usersContainer.repaint();
    }

    private JPanel createUserCard(User user) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(ModernTheme.FONT_BOLD);
        nameLabel.setForeground(ModernTheme.TEXT_LIGHT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(ModernTheme.FONT_SMALL);
        emailLabel.setForeground(ModernTheme.TEXT_DARK);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(emailLabel);

        JLabel typeLabel = new JLabel(user.getUserType());
        typeLabel.setFont(ModernTheme.FONT_SMALL);
        typeLabel.setForeground(ModernTheme.ACCENT);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(typeLabel);

        
        if (!user.getInterests().isEmpty()) {
            String interests = user.getInterests().stream()
                    .limit(3)
                    .map(i -> i.getName())
                    .collect(java.util.stream.Collectors.joining(", "));
            JLabel interestsLabel = new JLabel("Interests: " + interests);
            interestsLabel.setFont(ModernTheme.FONT_SMALL);
            interestsLabel.setForeground(ModernTheme.TEXT_DARK);
            interestsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(interestsLabel);
        }

        
        JLabel statsLabel = new JLabel(
                user.getFollowerCount() + " followers • " + user.getFollowingCount() + " following");
        statsLabel.setFont(ModernTheme.FONT_SMALL);
        statsLabel.setForeground(ModernTheme.TEXT_DARK);
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(statsLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            boolean isFollowing = currentUser.isFollowing(user.getId());
            ModernButton followButton = new ModernButton(isFollowing ? "Following" : "Follow");

            if (isFollowing) {
                followButton.setForeground(ModernTheme.TEXT_DARK);
            }

            followButton.addActionListener(e -> {
                if (currentUser.isFollowing(user.getId())) {
                    DataManager.getInstance().unfollowUser(currentUser.getId(), user.getId());
                    followButton.setText("Follow");
                    followButton.setForeground(Color.WHITE);
                } else {
                    DataManager.getInstance().followUser(currentUser.getId(), user.getId());
                    followButton.setText("Following");
                    followButton.setForeground(ModernTheme.TEXT_DARK);
                }
                refresh();
            });

            card.add(followButton, BorderLayout.EAST);
        }

        return card;
    }
}
