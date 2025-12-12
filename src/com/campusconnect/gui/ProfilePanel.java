package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfilePanel extends ModernPanel {
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel typeLabel;
    private JTextArea bioArea;
    private JLabel statsLabel;
    private JPanel postsContainer;
    private DefaultListModel<String> interestsModel;
    private JList<String> interestsList;

    public ProfilePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.FONT_BOLD);
        tabbedPane.setBackground(ModernTheme.BG_DARK);
        tabbedPane.setForeground(ModernTheme.TEXT_DARK);

        
        postsContainer = new JPanel();
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setOpaque(false);

        JScrollPane postsScroll = new JScrollPane(postsContainer);
        postsScroll.setOpaque(false);
        postsScroll.getViewport().setOpaque(false);
        postsScroll.setBorder(null);
        tabbedPane.addTab("My Posts", postsScroll);

        
        JPanel interestsPanel = createInterestsPanel();
        tabbedPane.addTab("Interests", interestsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        avatarLabel.setForeground(ModernTheme.TEXT_LIGHT);
        panel.add(avatarLabel, BorderLayout.WEST);

        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        nameLabel = new JLabel("Name");
        nameLabel.setFont(ModernTheme.FONT_HEADER);
        nameLabel.setForeground(ModernTheme.TEXT_LIGHT);
        infoPanel.add(nameLabel);

        emailLabel = new JLabel("email@example.com");
        emailLabel.setFont(ModernTheme.FONT_REGULAR);
        emailLabel.setForeground(ModernTheme.TEXT_DARK);
        infoPanel.add(emailLabel);

        typeLabel = new JLabel("Student");
        typeLabel.setFont(ModernTheme.FONT_SMALL);
        typeLabel.setForeground(ModernTheme.ACCENT);
        infoPanel.add(typeLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        bioArea = new JTextArea("Bio goes here...");
        bioArea.setFont(ModernTheme.FONT_REGULAR);
        bioArea.setForeground(ModernTheme.TEXT_LIGHT);
        bioArea.setBackground(ModernTheme.BG_DARK);
        bioArea.setEditable(false);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        infoPanel.add(bioArea);

        infoPanel.add(Box.createVerticalStrut(10));

        statsLabel = new JLabel("0 Followers • 0 Following");
        statsLabel.setFont(ModernTheme.FONT_BOLD);
        statsLabel.setForeground(ModernTheme.TEXT_LIGHT);
        infoPanel.add(statsLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        ModernButton editButton = new ModernButton("Edit Profile");
        editButton.addActionListener(e -> showEditDialog());
        buttonPanel.add(editButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createInterestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        interestsModel = new DefaultListModel<>();
        interestsList = new JList<>(interestsModel);
        interestsList.setBackground(ModernTheme.INPUT_BG);
        interestsList.setForeground(ModernTheme.TEXT_LIGHT);
        interestsList.setFont(ModernTheme.FONT_REGULAR);

        panel.add(new JScrollPane(interestsList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        ModernButton addButton = new ModernButton("Add Interest");
        addButton.addActionListener(e -> addInterest());
        buttonPanel.add(addButton);

        ModernButton removeButton = new ModernButton("Remove Selected");
        removeButton.addActionListener(e -> removeInterest());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showEditDialog() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null)
            return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Profile", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new ModernPanel();
        formPanel.setLayout(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel bioLabel = new JLabel("Bio:");
        bioLabel.setForeground(ModernTheme.TEXT_LIGHT);
        formPanel.add(bioLabel);

        JTextArea bioEdit = new JTextArea(user.getBio());
        bioEdit.setLineWrap(true);
        bioEdit.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(bioEdit));

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(ModernTheme.BG_DARK);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            user.setBio(bioEdit.getText().trim());
            DataManager.getInstance().saveUsers();
            refresh();
            dialog.dispose();
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refresh() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            typeLabel.setText(user.getUserType());
            bioArea.setText(user.getBio() != null && !user.getBio().isEmpty() ? user.getBio() : "No bio yet.");
            statsLabel.setText(user.getFollowerCount() + " Followers • " + user.getFollowingCount() + " Following");

            
            interestsModel.clear();
            for (Interest i : user.getInterests()) {
                interestsModel.addElement(i.getName() + " (" + i.getCategory() + ")");
            }

            
            postsContainer.removeAll();
            List<Post> myPosts = DataManager.getInstance().getPostsByUser(user.getId());

            if (myPosts.isEmpty()) {
                JLabel emptyLabel = new JLabel("You haven't posted anything yet.");
                emptyLabel.setForeground(ModernTheme.TEXT_DARK);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                postsContainer.add(Box.createVerticalStrut(20));
                postsContainer.add(emptyLabel);
            } else {
                for (Post post : myPosts) {
                    JPanel postCard = createPostCard(post);
                    postsContainer.add(postCard);
                    postsContainer.add(Box.createVerticalStrut(10));
                }
            }
            postsContainer.revalidate();
            postsContainer.repaint();
        }
    }

    private JPanel createPostCard(Post post) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
        JLabel timeLabel = new JLabel(post.getTimestamp().format(formatter));
        timeLabel.setForeground(ModernTheme.TEXT_DARK);
        header.add(timeLabel, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        
        JTextArea content = new JTextArea(post.getContent());
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setEditable(false);
        content.setOpaque(false);
        content.setForeground(ModernTheme.TEXT_LIGHT);
        card.add(content, BorderLayout.CENTER);

        
        JLabel stats = new JLabel("❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount());
        stats.setForeground(ModernTheme.TEXT_DARK);
        card.add(stats, BorderLayout.SOUTH);

        return card;
    }

    private void addInterest() {
        String name = JOptionPane.showInputDialog(this, "Enter Interest Name:");
        if (name != null && !name.trim().isEmpty()) {
            String category = JOptionPane.showInputDialog(this, "Enter Category (Hobby, Skill, Subject):");
            if (category == null || category.trim().isEmpty())
                category = "Other";

            Interest interest = new Interest(name, category);
            DataManager.getInstance().getCurrentUser().addInterest(interest);
            DataManager.getInstance().saveUsers();
            refresh();
        }
    }

    private void removeInterest() {
        String selected = interestsList.getSelectedValue();
        if (selected != null) {
            int lastParen = selected.lastIndexOf("(");
            if (lastParen > 0) {
                String name = selected.substring(0, lastParen).trim();
                String category = selected.substring(lastParen + 1, selected.length() - 1);
                Interest interest = new Interest(name, category);
                DataManager.getInstance().getCurrentUser().removeInterest(interest);
                DataManager.getInstance().saveUsers();
                refresh();
            }
        }
    }
}
