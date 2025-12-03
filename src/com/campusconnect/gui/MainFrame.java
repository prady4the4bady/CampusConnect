package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        setTitle("CampusConnect");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true); // Show window immediately

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(com.campusconnect.gui.components.ModernTheme.BG_DARK);

        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);
        // Dashboard will be initialized after login

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signUpPanel, "SIGNUP");

        add(mainPanel);

        // Initialize DataManager and seed in background to avoid blocking UI
        SwingUtilities.invokeLater(() -> {
            DataManager.getInstance();
            com.campusconnect.core.DataSeeder.seed();
            revalidate();
            repaint();
        });
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showSignUp() {
        cardLayout.show(mainPanel, "SIGNUP");
    }

    public void showDashboard() {
        if (dashboardPanel == null) {
            dashboardPanel = new DashboardPanel(this);
            mainPanel.add(dashboardPanel, "DASHBOARD");
        } else {
            dashboardPanel.refresh();
        }
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
