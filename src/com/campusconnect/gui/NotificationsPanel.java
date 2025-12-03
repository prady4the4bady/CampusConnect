package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsPanel extends ModernPanel {
    private JPanel notificationsContainer;
    private JLabel titleLabel;

    public NotificationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top: Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titleLabel = new JLabel("Notifications");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        ModernButton markReadButton = new ModernButton("Mark all as read");
        markReadButton.addActionListener(e -> markAllAsRead());
        headerPanel.add(markReadButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center: Notifications List
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private void markAllAsRead() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<Notification> notifs = DataManager.getInstance().getNotificationsForUser(currentUser.getId());
            for (Notification n : notifs) {
                if (!n.isRead()) {
                    DataManager.getInstance().markNotificationRead(n.getId());
                }
            }
            refresh();
        }
    }

    public void refresh() {
        notificationsContainer.removeAll();

        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        List<Notification> notifications = DataManager.getInstance().getNotificationsForUser(currentUser.getId());
        long unreadCount = DataManager.getInstance().getUnreadNotificationCount(currentUser.getId());

        titleLabel.setText("Notifications" + (unreadCount > 0 ? " (" + unreadCount + ")" : ""));

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications yet.");
            emptyLabel.setFont(ModernTheme.FONT_REGULAR);
            emptyLabel.setForeground(ModernTheme.TEXT_DARK);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationsContainer.add(Box.createVerticalStrut(50));
            notificationsContainer.add(emptyLabel);
        } else {
            for (Notification notif : notifications) {
                JPanel notifCard = createNotificationCard(notif);
                notificationsContainer.add(notifCard);
                notificationsContainer.add(Box.createVerticalStrut(10));
            }
        }

        notificationsContainer.revalidate();
        notificationsContainer.repaint();
    }

    private JPanel createNotificationCard(Notification notif) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(15, 10));

        // Highlight unread notifications
        if (!notif.isRead()) {
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.ACCENT, 1),
                    new EmptyBorder(15, 15, 15, 15)));
        } else {
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                    new EmptyBorder(15, 15, 15, 15)));
        }
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon based on type
        String icon = "🔔";
        switch (notif.getType()) {
            case "LIKE":
                icon = "❤️";
                break;
            case "COMMENT":
                icon = "💬";
                break;
            case "FOLLOW":
                icon = "👤";
                break;
            case "MENTION":
                icon = "@";
                break;
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(ModernTheme.TEXT_LIGHT);
        card.add(iconLabel, BorderLayout.WEST);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 5));
        contentPanel.setOpaque(false);

        JLabel messageLabel = new JLabel(notif.getMessage());
        messageLabel.setFont(notif.isRead() ? ModernTheme.FONT_REGULAR : ModernTheme.FONT_BOLD);
        messageLabel.setForeground(ModernTheme.TEXT_LIGHT);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
        JLabel timeLabel = new JLabel(notif.getTimestamp().format(formatter));
        timeLabel.setFont(ModernTheme.FONT_SMALL);
        timeLabel.setForeground(ModernTheme.TEXT_DARK);
        contentPanel.add(timeLabel, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }
}
