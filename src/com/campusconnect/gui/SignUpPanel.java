package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import java.awt.*;

public class SignUpPanel extends ModernPanel {
    private MainFrame mainFrame;
    private ModernTextField nameField;
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private ModernPasswordField confirmPasswordField;
    private JComboBox<String> userTypeCombo;
    private JLabel statusLabel;

    public SignUpPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Join CampusConnect Community");
        subtitleLabel.setFont(ModernTheme.FONT_REGULAR);
        subtitleLabel.setForeground(ModernTheme.TEXT_DARK);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        add(subtitleLabel, gbc);

        gbc.gridwidth = 1;

        // Name
        gbc.gridy++;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(ModernTheme.TEXT_LIGHT);
        nameLabel.setFont(ModernTheme.FONT_REGULAR);
        gbc.gridx = 0;
        add(nameLabel, gbc);

        nameField = new ModernTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Email
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(ModernTheme.TEXT_LIGHT);
        emailLabel.setFont(ModernTheme.FONT_REGULAR);
        gbc.gridx = 0;
        add(emailLabel, gbc);

        emailField = new ModernTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Email hint
        JLabel emailHint = new JLabel("Use your institutional email");
        emailHint.setFont(ModernTheme.FONT_SMALL);
        emailHint.setForeground(ModernTheme.TEXT_DARK);
        gbc.gridx = 1;
        gbc.gridy++;
        add(emailHint, gbc);

        // User Type
        gbc.gridy++;
        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setForeground(ModernTheme.TEXT_LIGHT);
        userTypeLabel.setFont(ModernTheme.FONT_REGULAR);
        gbc.gridx = 0;
        add(userTypeLabel, gbc);

        userTypeCombo = new JComboBox<>(new String[] { "Student", "Faculty", "Alumni" });
        userTypeCombo.setFont(ModernTheme.FONT_REGULAR);
        userTypeCombo.setBackground(ModernTheme.INPUT_BG);
        userTypeCombo.setForeground(ModernTheme.TEXT_LIGHT);
        gbc.gridx = 1;
        add(userTypeCombo, gbc);

        // Password
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(ModernTheme.TEXT_LIGHT);
        passLabel.setFont(ModernTheme.FONT_REGULAR);
        gbc.gridx = 0;
        add(passLabel, gbc);

        passwordField = new ModernPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Confirm Password
        gbc.gridy++;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(ModernTheme.TEXT_LIGHT);
        confirmLabel.setFont(ModernTheme.FONT_REGULAR);
        gbc.gridx = 0;
        add(confirmLabel, gbc);

        confirmPasswordField = new ModernPasswordField(20);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // Sign Up Button
        ModernButton signUpButton = new ModernButton("Sign Up");
        signUpButton.addActionListener(e -> handleSignUp());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(signUpButton, gbc);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        add(statusLabel, gbc);

        // Back to Login
        ModernButton backButton = new ModernButton("Back to Login");
        backButton.addActionListener(e -> mainFrame.showLogin());
        gbc.gridy++;
        add(backButton, gbc);
    }

    private void handleSignUp() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!email.contains("@")) {
            statusLabel.setText("Invalid email format");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Check if email already exists
        for (User u : DataManager.getInstance().getUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                statusLabel.setText("Email already registered");
                statusLabel.setForeground(Color.RED);
                return;
            }
        }

        // Create new user
        String userId = "U" + System.currentTimeMillis();
        User newUser = UserFactory.createUser(userType, userId, name, email, password);

        if (newUser != null) {
            DataManager.getInstance().addUser(newUser);
            statusLabel.setText("Account created successfully!");
            statusLabel.setForeground(ModernTheme.ACCENT);

            // Auto-login after 1 second
            Timer timer = new Timer(1000, e -> {
                DataManager.getInstance().setCurrentUser(newUser);
                mainFrame.showDashboard();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            statusLabel.setText("Error creating account");
            statusLabel.setForeground(Color.RED);
        }
    }
}
