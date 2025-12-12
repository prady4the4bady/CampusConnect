package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import com.campusconnect.core.WalkieTalkieChannel;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class WalkieTalkiePanel extends ModernPanel {
    private DefaultListModel<String> channelModel;
    private JList<String> channelList;
    private JLabel statusLabel;
    private ModernButton talkButton;
    private WalkieTalkieChannel currentChannel;
    private Timer visualizerTimer;
    private JPanel visualizerPanel;

    public WalkieTalkiePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JPanel sidebar = new ModernPanel(new BorderLayout(10, 10));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel channelsTitle = new JLabel("Channels");
        channelsTitle.setFont(ModernTheme.FONT_HEADER);
        channelsTitle.setForeground(ModernTheme.TEXT_LIGHT);
        sidebar.add(channelsTitle, BorderLayout.NORTH);

        channelModel = new DefaultListModel<>();
        channelList = new JList<>(channelModel);
        channelList.setBackground(ModernTheme.BG_DARK);
        channelList.setForeground(ModernTheme.TEXT_LIGHT);
        channelList.setFont(ModernTheme.FONT_REGULAR);
        channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelList.setFixedCellHeight(30);
        channelList.addListSelectionListener(e -> joinChannel());

        JScrollPane scrollPane = new JScrollPane(channelList);
        scrollPane.setBorder(null);
        sidebar.add(scrollPane, BorderLayout.CENTER);

        ModernButton createBtn = new ModernButton("Create Channel");
        createBtn.addActionListener(e -> createChannel());
        sidebar.add(createBtn, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        
        JPanel mainArea = new ModernPanel(new GridBagLayout());
        mainArea.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        statusLabel = new JLabel("Select a channel to start");
        statusLabel.setFont(ModernTheme.FONT_TITLE);
        statusLabel.setForeground(ModernTheme.TEXT_LIGHT);
        mainArea.add(statusLabel, gbc);

        
        visualizerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (talkButton.getModel().isPressed()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(ModernTheme.ACCENT);

                    for (int i = 0; i < 15; i++) {
                        int h = (int) (Math.random() * 60) + 20;
                        g2d.fillRoundRect(i * 18 + 20, 50 - h / 2, 10, h, 5, 5);
                    }
                }
            }
        };
        visualizerPanel.setPreferredSize(new Dimension(300, 100));
        visualizerPanel.setOpaque(false);
        gbc.gridy++;
        mainArea.add(visualizerPanel, gbc);

        
        talkButton = new ModernButton("HOLD TO TALK");
        talkButton.setPreferredSize(new Dimension(220, 220));
        talkButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        talkButton.setEnabled(false);
        
        talkButton.setBackground(ModernTheme.BG_DARK);
        talkButton.setForeground(Color.GRAY);

        talkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentChannel != null && talkButton.isEnabled()) {
                    statusLabel.setText("Broadcasting...");
                    statusLabel.setForeground(ModernTheme.ACCENT);
                    talkButton.setBackground(ModernTheme.ACCENT);
                    talkButton.setForeground(Color.WHITE);
                    visualizerTimer.start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentChannel != null && talkButton.isEnabled()) {
                    statusLabel.setText("Connected: " + currentChannel.getName());
                    statusLabel.setForeground(ModernTheme.TEXT_LIGHT);
                    talkButton.setBackground(ModernTheme.BG_DARK);
                    talkButton.setForeground(ModernTheme.TEXT_LIGHT);
                    visualizerTimer.stop();
                    visualizerPanel.repaint();
                }
            }
        });

        gbc.gridy++;
        mainArea.add(talkButton, gbc);

        add(mainArea, BorderLayout.CENTER);

        
        visualizerTimer = new Timer(50, e -> visualizerPanel.repaint());

        refresh();
    }

    public void refresh() {
        channelModel.clear();
        for (WalkieTalkieChannel c : DataManager.getInstance().getChannels()) {
            channelModel.addElement(c.getName() + (c.isPrivate() ? " 🔒" : ""));
        }
    }

    private void joinChannel() {
        if (channelList.getValueIsAdjusting())
            return;

        String selected = channelList.getSelectedValue();
        if (selected == null)
            return;

        String name = selected.replace(" 🔒", "");
        for (WalkieTalkieChannel c : DataManager.getInstance().getChannels()) {
            if (c.getName().equals(name)) {
                if (c.isPrivate()) {
                    String code = JOptionPane.showInputDialog(this, "Enter Invite Code:");
                    if (code == null || !code.equals(c.getInviteCode())) {
                        JOptionPane.showMessageDialog(this, "Invalid Code!", "Error", JOptionPane.ERROR_MESSAGE);
                        channelList.clearSelection();
                        return;
                    }
                }
                currentChannel = c;
                statusLabel.setText("Connected: " + c.getName());
                statusLabel.setForeground(ModernTheme.TEXT_LIGHT);
                talkButton.setEnabled(true);
                talkButton.setForeground(ModernTheme.TEXT_LIGHT);
                return;
            }
        }
    }

    private void createChannel() {
        String name = JOptionPane.showInputDialog(this, "Channel Name:");
        if (name != null && !name.trim().isEmpty()) {
            int type = JOptionPane.showConfirmDialog(this, "Is this channel Private?", "Channel Type",
                    JOptionPane.YES_NO_OPTION);
            boolean isPrivate = (type == JOptionPane.YES_OPTION);
            String code = isPrivate ? UUID.randomUUID().toString().substring(0, 6).toUpperCase() : "";

            WalkieTalkieChannel c = new WalkieTalkieChannel(
                    "WC" + System.currentTimeMillis(),
                    name,
                    isPrivate,
                    code);
            DataManager.getInstance().addChannel(c);
            refresh();

            if (isPrivate) {
                JOptionPane.showMessageDialog(this, "Channel Created! Invite Code: " + code);
            }
        }
    }
}
