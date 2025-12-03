package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import com.campusconnect.core.User;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends ModernPanel {
    private MainFrame mainFrame;
    private JLabel welcomeLabel;
    private DiscoverHomePanel discoverHomePanel;
    private FeedPanel feedPanel;
    private ExplorePanel explorePanel;
    private NotificationsPanel notificationsPanel;
    private GroupsPanel groupsPanel;
    private EventsPanel eventsPanel;
    private ChatPanel chatPanel;
    private ProfilePanel profilePanel;
    private WalkieTalkiePanel walkieTalkiePanel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new ModernPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(ModernTheme.FONT_HEADER);
        welcomeLabel.setForeground(ModernTheme.TEXT_LIGHT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        ModernButton logoutButton = new ModernButton("Logout");
        logoutButton.addActionListener(e -> {
            DataManager.getInstance().setCurrentUser(null);
            this.mainFrame.showLogin();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        ModernTheme.styleModernTabs(tabbedPane);

        // Feed Tab (NEW - Social Media Feed)
        feedPanel = new FeedPanel();
        tabbedPane.addTab("🏠 Feed", feedPanel);

        // Explore Tab (NEW - User Discovery)
        explorePanel = new ExplorePanel();
        tabbedPane.addTab("🔍 Explore", explorePanel);

        // Notifications Tab (NEW)
        notificationsPanel = new NotificationsPanel();
        tabbedPane.addTab("🔔 Alerts", notificationsPanel);

        // Discover Tab - NEW Enhanced Version with Stats
        discoverHomePanel = new DiscoverHomePanel();
        tabbedPane.addTab("✨ Discover", discoverHomePanel);

        // Groups Tab
        groupsPanel = new GroupsPanel();
        tabbedPane.addTab("Groups", groupsPanel);

        // Events Tab
        eventsPanel = new EventsPanel();
        tabbedPane.addTab("Events", eventsPanel);

        // Chat Tab
        chatPanel = new ChatPanel();
        tabbedPane.addTab("Chat", chatPanel);

        // Walkie-Talkie Tab
        walkieTalkiePanel = new WalkieTalkiePanel();
        tabbedPane.addTab("Walkie-Talkie", walkieTalkiePanel);

        // Profile Tab
        profilePanel = new ProfilePanel();
        tabbedPane.addTab("Profile", profilePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Add change listener to refresh tabs when selected
        tabbedPane.addChangeListener(e -> refresh());

        refresh();
    }

    public void refresh() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName() + " (" + user.getUserType() + ")");

            // Refresh all panels
            if (feedPanel != null)
                feedPanel.refresh();
            if (explorePanel != null)
                explorePanel.refresh();
            if (notificationsPanel != null)
                notificationsPanel.refresh();
            if (discoverHomePanel != null)
                discoverHomePanel.refresh();
            if (groupsPanel != null)
                groupsPanel.refresh();
            if (eventsPanel != null)
                eventsPanel.refresh();
            if (chatPanel != null)
                chatPanel.refresh();
            if (profilePanel != null)
                profilePanel.refresh();
            if (walkieTalkiePanel != null)
                walkieTalkiePanel.refresh();
        }
    }
}
