package com.campusconnect.gui;

import com.campusconnect.core.*;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GroupsPanel extends ModernPanel {
    private JPanel groupsContainer;

    public GroupsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Campus Groups");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        ModernButton createButton = new ModernButton("Create Group");
        createButton.addActionListener(e -> createGroup());
        headerPanel.add(createButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Groups List
        groupsContainer = new JPanel();
        groupsContainer.setLayout(new BoxLayout(groupsContainer, BoxLayout.Y_AXIS));
        groupsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(groupsContainer);
        ModernTheme.applyModernScrollbar(scrollPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        groupsContainer.removeAll();
        Map<String, Group> groups = DataManager.getInstance().getGroups();

        if (groups.isEmpty()) {
            JLabel emptyLabel = new JLabel("No groups available.");
            emptyLabel.setForeground(ModernTheme.TEXT_DARK);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            groupsContainer.add(Box.createVerticalStrut(20));
            groupsContainer.add(emptyLabel);
        } else {
            for (Group g : groups.values()) {
                JPanel groupCard = createGroupCard(g);
                groupsContainer.add(groupCard);
                groupsContainer.add(Box.createVerticalStrut(10));
            }
        }
        groupsContainer.revalidate();
        groupsContainer.repaint();
    }

    private JPanel createGroupCard(Group g) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(g.getName());
        nameLabel.setFont(ModernTheme.FONT_BOLD);
        nameLabel.setForeground(ModernTheme.TEXT_LIGHT);
        infoPanel.add(nameLabel);

        JLabel descLabel = new JLabel(g.getDescription());
        descLabel.setFont(ModernTheme.FONT_REGULAR);
        descLabel.setForeground(ModernTheme.TEXT_DARK);
        infoPanel.add(descLabel);

        JLabel membersLabel = new JLabel(g.getMemberIds().size() + " members");
        membersLabel.setFont(ModernTheme.FONT_SMALL);
        membersLabel.setForeground(ModernTheme.ACCENT);
        infoPanel.add(membersLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Action Buttons
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);

            boolean isMember = g.getMemberIds().contains(currentUser.getId());
            boolean isCouncil = g.isCouncil(currentUser.getId());
            boolean isFaculty = g.isFacultyIncharge(currentUser.getId());

            // Faculty manages council
            if (isFaculty) {
                ModernButton manageCouncilBtn = new ModernButton("Manage Council");
                manageCouncilBtn.addActionListener(e -> showCouncilManagement(g));
                buttonPanel.add(manageCouncilBtn);
            }

            // Council approves requests
            if (isCouncil || isFaculty) {
                int pendingCount = DataManager.getInstance().getPendingRequestsForGroup(g.getId()).size();
                ModernButton requestsBtn = new ModernButton("Requests (" + pendingCount + ")");
                requestsBtn.addActionListener(e -> showPendingRequests(g));
                buttonPanel.add(requestsBtn);
            }

            // Join/Request button
            if (isMember) {
                ModernButton joinedBtn = new ModernButton("Joined");
                joinedBtn.setEnabled(false);
                joinedBtn.setForeground(ModernTheme.TEXT_DARK);
                buttonPanel.add(joinedBtn);
            } else {
                ModernButton requestBtn = new ModernButton("Request to Join");
                requestBtn.addActionListener(e -> requestToJoin(g));
                buttonPanel.add(requestBtn);
            }

            card.add(buttonPanel, BorderLayout.EAST);
        }

        return card;
    }

    private void requestToJoin(Group group) {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        // Check if already requested
        List<GroupRequest> userRequests = DataManager.getInstance().getGroupRequestsForUser(currentUser.getId());
        boolean alreadyRequested = userRequests.stream()
                .anyMatch(r -> r.getGroupId().equals(group.getId()) && r.isPending());

        if (alreadyRequested) {
            JOptionPane.showMessageDialog(this, "You have already requested to join this group.");
            return;
        }

        GroupRequest request = new GroupRequest(
                "GR" + System.currentTimeMillis(),
                group.getId(),
                currentUser.getId());
        DataManager.getInstance().addGroupRequest(request);
        JOptionPane.showMessageDialog(this, "Join request sent! Council will review it.");
        refresh();
    }

    private void showPendingRequests(Group group) {
        List<GroupRequest> requests = DataManager.getInstance().getPendingRequestsForGroup(group.getId());

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pending Requests", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel content = new ModernPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (requests.isEmpty()) {
            JLabel empty = new JLabel("No pending requests");
            empty.setForeground(ModernTheme.TEXT_DARK);
            content.add(empty);
        } else {
            for (GroupRequest req : requests) {
                User user = DataManager.getInstance().getUserById(req.getUserId());
                if (user != null) {
                    JPanel reqPanel = new ModernPanel(new BorderLayout(10, 5));
                    reqPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                    JLabel userLabel = new JLabel(user.getName() + " (" + user.getEmail() + ")");
                    userLabel.setForeground(ModernTheme.TEXT_LIGHT);
                    reqPanel.add(userLabel, BorderLayout.CENTER);

                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    btnPanel.setOpaque(false);

                    ModernButton approveBtn = new ModernButton("Approve");
                    approveBtn.addActionListener(e -> {
                        DataManager.getInstance().approveGroupRequest(req.getId());
                        dialog.dispose();
                        refresh();
                        JOptionPane.showMessageDialog(this, "Request approved!");
                    });

                    ModernButton rejectBtn = new ModernButton("Reject");
                    rejectBtn.addActionListener(e -> {
                        DataManager.getInstance().rejectGroupRequest(req.getId());
                        dialog.dispose();
                        refresh();
                        JOptionPane.showMessageDialog(this, "Request rejected.");
                    });

                    btnPanel.add(approveBtn);
                    btnPanel.add(rejectBtn);
                    reqPanel.add(btnPanel, BorderLayout.EAST);

                    content.add(reqPanel);
                    content.add(Box.createVerticalStrut(10));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        ModernTheme.applyModernScrollbar(scrollPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void showCouncilManagement(Group group) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Manage Council - " + group.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel content = new ModernPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Current council members
        JLabel title = new JLabel("Council Members:");
        title.setFont(ModernTheme.FONT_BOLD);
        title.setForeground(ModernTheme.TEXT_LIGHT);
        content.add(title);
        content.add(Box.createVerticalStrut(10));

        List<User> councilUsers = new ArrayList<>();
        for (String userId : group.getCouncilMembers()) {
            User u = DataManager.getInstance().getUserById(userId);
            if (u != null)
                councilUsers.add(u);
        }

        if (councilUsers.isEmpty()) {
            JLabel empty = new JLabel("No council members yet");
            empty.setForeground(ModernTheme.TEXT_DARK);
            content.add(empty);
        } else {
            for (User u : councilUsers) {
                JPanel memberPanel = new JPanel(new BorderLayout());
                memberPanel.setOpaque(false);
                memberPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

                JLabel userLabel = new JLabel(u.getName() + " (" + u.getUserType() + ")");
                userLabel.setForeground(ModernTheme.TEXT_LIGHT);
                memberPanel.add(userLabel, BorderLayout.CENTER);

                ModernButton removeBtn = new ModernButton("Remove");
                removeBtn.addActionListener(e -> {
                    group.removeCouncilMember(u.getId());
                    DataManager.getInstance().saveGroups();
                    dialog.dispose();
                    showCouncilManagement(group);
                });
                memberPanel.add(removeBtn, BorderLayout.EAST);

                content.add(memberPanel);
            }
        }

        content.add(Box.createVerticalStrut(20));

        // Add council member
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setOpaque(false);

        ModernButton addBtn = new ModernButton("Add Council Member");
        addBtn.addActionListener(e -> {
            // Get all group members who aren't council
            List<User> eligibleMembers = new ArrayList<>();
            for (String memberId : group.getMemberIds()) {
                if (!group.isCouncil(memberId)) {
                    User u = DataManager.getInstance().getUserById(memberId);
                    if (u != null)
                        eligibleMembers.add(u);
                }
            }

            if (eligibleMembers.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No eligible members to add.");
                return;
            }

            String[] options = eligibleMembers.stream()
                    .map(u -> u.getName() + " (" + u.getEmail() + ")")
                    .toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(
                    dialog, "Select member to add to council:", "Add Council Member",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (selected != null) {
                int idx = java.util.Arrays.asList(options).indexOf(selected);
                User selectedUser = eligibleMembers.get(idx);
                group.addCouncilMember(selectedUser.getId());
                DataManager.getInstance().saveGroups();
                dialog.dispose();
                showCouncilManagement(group);
            }
        });
        addPanel.add(addBtn);
        content.add(addPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        ModernTheme.applyModernScrollbar(scrollPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void createGroup() {
        String name = JOptionPane.showInputDialog(this, "Enter Group Name:");
        if (name != null && !name.trim().isEmpty()) {
            String desc = JOptionPane.showInputDialog(this, "Enter Description:");
            if (desc == null)
                desc = "";

            String id = "G" + System.currentTimeMillis();
            Group g = new Group(id, name, desc);

            User currentUser = DataManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                g.addMember(currentUser.getId());

                // If faculty, set as incharge
                if (currentUser.getUserType().equals("TEACHER")) {
                    g.setFacultyIncharge(currentUser.getId());
                    g.addCouncilMember(currentUser.getId());
                }
            }

            DataManager.getInstance().addGroup(g);
            refresh();
        }
    }
}
