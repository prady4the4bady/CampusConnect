package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.ai.ContentModerator;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeedPanel extends ModernPanel {
    private JPanel postsContainer;
    private JTextArea createPostArea;
    private JLabel characterCountLabel;
    private static final int MAX_POST_LENGTH = 500;

    public FeedPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top: Create Post Section
        JPanel createPostPanel = createPostCreationPanel();
        add(createPostPanel, BorderLayout.NORTH);

        // Center: Posts Feed
        postsContainer = new JPanel();
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(postsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createPostCreationPanel() {
        JPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));

        // Title
        JLabel titleLabel = new JLabel("What's on your mind?");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Text Area
        createPostArea = new JTextArea(3, 40);
        createPostArea.setFont(ModernTheme.FONT_REGULAR);
        createPostArea.setLineWrap(true);
        createPostArea.setWrapStyleWord(true);
        createPostArea.setBackground(ModernTheme.INPUT_BG);
        createPostArea.setForeground(ModernTheme.TEXT_LIGHT);
        createPostArea.setCaretColor(ModernTheme.ACCENT);
        createPostArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Character counter
        characterCountLabel = new JLabel("0 / " + MAX_POST_LENGTH);
        characterCountLabel.setFont(ModernTheme.FONT_SMALL);
        characterCountLabel.setForeground(ModernTheme.TEXT_DARK);

        createPostArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCount();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCount();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCount();
            }

            private void updateCount() {
                int length = createPostArea.getText().length();
                characterCountLabel.setText(length + " / " + MAX_POST_LENGTH);
                if (length > MAX_POST_LENGTH) {
                    characterCountLabel.setForeground(Color.RED);
                } else {
                    characterCountLabel.setForeground(ModernTheme.TEXT_DARK);
                }
            }
        });

        JScrollPane textScrollPane = new JScrollPane(createPostArea);
        textScrollPane.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER));
        panel.add(textScrollPane, BorderLayout.CENTER);

        // Bottom: Post Button and Counter
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(characterCountLabel, BorderLayout.WEST);

        ModernButton postButton = new ModernButton("Post");
        postButton.addActionListener(e -> handleCreatePost());
        bottomPanel.add(postButton, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleCreatePost() {
        String content = createPostArea.getText().trim();

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Post cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (content.length() > MAX_POST_LENGTH) {
            JOptionPane.showMessageDialog(this, "Post exceeds maximum length of " + MAX_POST_LENGTH + " characters!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Content moderation
        String moderationError = ContentModerator.checkContent(content);
        if (moderationError != null) {
            JOptionPane.showMessageDialog(this, moderationError, "Content Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create post
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String postId = "P" + System.currentTimeMillis();
            Post post = new Post(postId, currentUser.getId(), content);
            DataManager.getInstance().addPost(post);

            createPostArea.setText("");
            refresh();

            JOptionPane.showMessageDialog(this, "Post created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refresh() {
        postsContainer.removeAll();

        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        // Get feed posts (from followed users + own posts)
        List<Post> feedPosts = DataManager.getInstance().getFeedPosts(currentUser.getId());

        if (feedPosts.isEmpty()) {
            JLabel emptyLabel = new JLabel("No posts yet. Follow users or create your first post!");
            emptyLabel.setFont(ModernTheme.FONT_REGULAR);
            emptyLabel.setForeground(ModernTheme.TEXT_DARK);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            postsContainer.add(Box.createVerticalStrut(50));
            postsContainer.add(emptyLabel);
        } else {
            for (Post post : feedPosts) {
                JPanel postCard = createPostCard(post);
                postsContainer.add(postCard);
                postsContainer.add(Box.createVerticalStrut(10));
            }
        }

        postsContainer.revalidate();
        postsContainer.repaint();
    }

    private JPanel createPostCard(Post post) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Author info
        User author = DataManager.getInstance().getUserById(post.getAuthorId());
        String authorName = author != null ? author.getName() : "Unknown";

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel authorLabel = new JLabel(authorName);
        authorLabel.setFont(ModernTheme.FONT_BOLD);
        authorLabel.setForeground(ModernTheme.TEXT_LIGHT);
        headerPanel.add(authorLabel, BorderLayout.WEST);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        JLabel timeLabel = new JLabel(post.getTimestamp().format(formatter));
        timeLabel.setFont(ModernTheme.FONT_SMALL);
        timeLabel.setForeground(ModernTheme.TEXT_DARK);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Post content
        JTextArea contentArea = new JTextArea(post.getContent());
        contentArea.setFont(ModernTheme.FONT_REGULAR);
        contentArea.setForeground(ModernTheme.TEXT_LIGHT);
        contentArea.setBackground(ModernTheme.PANEL_BG);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(null);
        card.add(contentArea, BorderLayout.CENTER);

        // Actions (Like, Comment)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionsPanel.setOpaque(false);

        User currentUser = DataManager.getInstance().getCurrentUser();
        boolean isLiked = currentUser != null && post.isLikedBy(currentUser.getId());

        JButton likeButton = new JButton(isLiked ? "❤️ " + post.getLikeCount() : "🤍 " + post.getLikeCount());
        likeButton.setFont(ModernTheme.FONT_REGULAR);
        likeButton.setForeground(ModernTheme.TEXT_LIGHT);
        likeButton.setContentAreaFilled(false);
        likeButton.setBorderPainted(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeButton.addActionListener(e -> handleLike(post, likeButton));
        actionsPanel.add(likeButton);

        JButton commentButton = new JButton("💬 " + post.getCommentCount());
        commentButton.setFont(ModernTheme.FONT_REGULAR);
        commentButton.setForeground(ModernTheme.TEXT_LIGHT);
        commentButton.setContentAreaFilled(false);
        commentButton.setBorderPainted(false);
        commentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        commentButton.addActionListener(e -> handleComment(post));
        actionsPanel.add(commentButton);

        card.add(actionsPanel, BorderLayout.SOUTH);

        return card;
    }

    private void handleLike(Post post, JButton likeButton) {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        if (post.isLikedBy(currentUser.getId())) {
            post.removeLike(currentUser.getId());
            likeButton.setText("🤍 " + post.getLikeCount());
        } else {
            post.addLike(currentUser.getId());
            likeButton.setText("❤️ " + post.getLikeCount());

            // Create notification for post author
            if (!post.getAuthorId().equals(currentUser.getId())) {
                String notifId = "N" + System.currentTimeMillis();
                Notification notif = new Notification(notifId, post.getAuthorId(), "LIKE",
                        currentUser.getName() + " liked your post", post.getId());
                DataManager.getInstance().addNotification(notif);
            }
        }

        DataManager.getInstance().savePosts();
    }

    private void handleComment(Post post) {
        String comment = JOptionPane.showInputDialog(this, "Write your comment:");
        if (comment != null && !comment.trim().isEmpty()) {
            User currentUser = DataManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                String commentId = "C" + System.currentTimeMillis();
                Comment newComment = new Comment(commentId, post.getId(), currentUser.getId(), comment.trim());
                DataManager.getInstance().addComment(post.getId(), newComment);

                // Create notification
                if (!post.getAuthorId().equals(currentUser.getId())) {
                    String notifId = "N" + System.currentTimeMillis();
                    Notification notif = new Notification(notifId, post.getAuthorId(), "COMMENT",
                            currentUser.getName() + " commented on your post", post.getId());
                    DataManager.getInstance().addNotification(notif);
                }

                refresh();
            }
        }
    }
}
