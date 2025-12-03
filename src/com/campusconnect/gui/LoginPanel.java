package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import com.campusconnect.core.User;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends ModernPanel {
    private MainFrame mainFrame;
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private JLabel statusLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("CampusConnect");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(ModernTheme.TEXT_LIGHT);
        emailLabel.setFont(ModernTheme.FONT_REGULAR);
        add(emailLabel, gbc);

        emailField = new ModernTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(ModernTheme.TEXT_LIGHT);
        passLabel.setFont(ModernTheme.FONT_REGULAR);
        add(passLabel, gbc);

        passwordField = new ModernPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        ModernButton loginButton = new ModernButton("Login");
        loginButton.addActionListener(this::handleLogin);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        // Sign Up Button
        ModernButton signUpButton = new ModernButton("Create Account");
        signUpButton.addActionListener(e -> mainFrame.showSignUp());
        gbc.gridy++;
        add(signUpButton, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        add(statusLabel, gbc);

        // Demo User Hint
        JLabel hintLabel = new JLabel("Demo: aarav@bits-dubai.ac.ae / pass123");
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        add(hintLabel, gbc);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        // Disable inputs during login
        setControlsEnabled(false);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(ModernTheme.ACCENT);

        // Run login in background
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                // Simulate small delay for better UX or if DB is slow
                Thread.sleep(500);
                return DataManager.getInstance().login(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        statusLabel.setText("Login successful!");
                        // Initialize dashboard in background if needed, or just show it
                        mainFrame.showDashboard();
                    } else {
                        statusLabel.setText("Invalid email or password.");
                        statusLabel.setForeground(Color.RED);
                        setControlsEnabled(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Error during login.");
                    statusLabel.setForeground(Color.RED);
                    setControlsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void setControlsEnabled(boolean enabled) {
        emailField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        // Find buttons to disable/enable
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(enabled);
            }
        }
    }
}
