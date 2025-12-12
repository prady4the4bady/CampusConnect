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

        
        JTabbedPane tabbedPane = new JTabbedPane();
        ModernTheme.styleModernTabs(tabbedPane);

        
        feedPanel = new FeedPanel();
        tabbedPane.addTab("🏠 Feed", feedPanel);

        
        explorePanel = new ExplorePanel();
        tabbedPane.addTab("🔍 Explore", explorePanel);

        
        notificationsPanel = new NotificationsPanel();
        tabbedPane.addTab("🔔 Alerts", notificationsPanel);

        
        discoverHomePanel = new DiscoverHomePanel();
        tabbedPane.addTab("✨ Discover", discoverHomePanel);

        
        groupsPanel = new GroupsPanel();
        tabbedPane.addTab("Groups", groupsPanel);

        
        eventsPanel = new EventsPanel();
        tabbedPane.addTab("Events", eventsPanel);

        
        chatPanel = new ChatPanel();
        tabbedPane.addTab("Chat", chatPanel);

        
        walkieTalkiePanel = new WalkieTalkiePanel();
        tabbedPane.addTab("Walkie-Talkie", walkieTalkiePanel);

        
        profilePanel = new ProfilePanel();
        tabbedPane.addTab("Profile", profilePanel);

        add(tabbedPane, BorderLayout.CENTER);

        
        tabbedPane.addChangeListener(e -> refresh());

        refresh();
    }

    public void refresh() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getName() + " (" + user.getUserType() + ")");

            
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
