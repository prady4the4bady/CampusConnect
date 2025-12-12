package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import com.campusconnect.core.Message;
import com.campusconnect.core.User;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ChatPanel extends ModernPanel {
    private JPanel chatContainer;
    private ModernTextField messageField;

    public ChatPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JLabel titleLabel = new JLabel("Campus Chat");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);

        
        chatContainer = new JPanel();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(chatContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);

        messageField = new ModernTextField(30);
        messageField.addActionListener(e -> sendMessage());
        inputPanel.add(messageField, BorderLayout.CENTER);

        ModernButton sendButton = new ModernButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        chatContainer.removeAll();
        User currentUser = DataManager.getInstance().getCurrentUser();

        for (Message m : DataManager.getInstance().getMessages()) {
            boolean isMe = currentUser != null && m.getSenderId().equals(currentUser.getId());
            JPanel bubble = createMessageBubble(m, isMe);
            chatContainer.add(bubble);
            chatContainer.add(Box.createVerticalStrut(10));
        }

        chatContainer.revalidate();
        chatContainer.repaint();

        
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) chatContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createMessageBubble(Message m, boolean isMe) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel bubble = new ModernPanel(); 
        bubble.setLayout(new BorderLayout(5, 5));
        bubble.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (isMe) {
            bubble.setBackground(ModernTheme.ACCENT);
        } else {
            bubble.setBackground(ModernTheme.PANEL_BG);
        }

        
        if (!isMe) {
            String senderName = "Unknown";
            User u = DataManager.getInstance().getUserById(m.getSenderId());
            if (u != null)
                senderName = u.getName();

            JLabel nameLabel = new JLabel(senderName);
            nameLabel.setFont(ModernTheme.FONT_BOLD);
            nameLabel.setForeground(ModernTheme.ACCENT);
            bubble.add(nameLabel, BorderLayout.NORTH);
        }

        
        JTextArea content = new JTextArea(m.getContent());
        content.setFont(ModernTheme.FONT_REGULAR);
        content.setForeground(ModernTheme.TEXT_LIGHT);
        content.setOpaque(false);
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        bubble.add(content, BorderLayout.CENTER);

        
        JLabel timeLabel = new JLabel(m.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setFont(ModernTheme.FONT_SMALL);
        timeLabel.setForeground(ModernTheme.TEXT_DARK);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        if (isMe) {
            row.add(bubble, BorderLayout.EAST);
        } else {
            row.add(bubble, BorderLayout.WEST);
        }

        return row;
    }

    private void sendMessage() {
        String content = messageField.getText();
        if (content != null && !content.trim().isEmpty()) {
            User currentUser = DataManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                Message m = new Message(currentUser.getId(), content);
                DataManager.getInstance().addMessage(m);
                messageField.setText("");
                refresh();
            }
        }
    }
}
