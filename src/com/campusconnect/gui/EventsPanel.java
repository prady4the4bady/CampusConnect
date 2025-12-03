package com.campusconnect.gui;

import com.campusconnect.core.DataManager;
import com.campusconnect.core.Event;
import com.campusconnect.gui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventsPanel extends ModernPanel {
    private JPanel eventsContainer;

    public EventsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Campus Events");
        titleLabel.setFont(ModernTheme.FONT_HEADER);
        titleLabel.setForeground(ModernTheme.TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        ModernButton postButton = new ModernButton("Post Event");
        postButton.addActionListener(e -> postEvent());
        headerPanel.add(postButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Events List
        eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(eventsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        eventsContainer.removeAll();
        List<Event> events = new ArrayList<>(DataManager.getInstance().getEvents());

        if (events.isEmpty()) {
            JLabel emptyLabel = new JLabel("No upcoming events.");
            emptyLabel.setForeground(ModernTheme.TEXT_DARK);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            eventsContainer.add(Box.createVerticalStrut(20));
            eventsContainer.add(emptyLabel);
        } else {
            for (Event e : events) {
                JPanel eventCard = createEventCard(e);
                eventsContainer.add(eventCard);
                eventsContainer.add(Box.createVerticalStrut(10));
            }
        }
        eventsContainer.revalidate();
        eventsContainer.repaint();
    }

    private JPanel createEventCard(Event e) {
        JPanel card = new ModernPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Date Box (Left)
        JPanel datePanel = new JPanel(new GridLayout(2, 1));
        datePanel.setOpaque(false);
        datePanel.setPreferredSize(new Dimension(60, 60));

        JLabel monthLabel = new JLabel(e.getDateTime().getMonth().name().substring(0, 3));
        monthLabel.setFont(ModernTheme.FONT_BOLD);
        monthLabel.setForeground(ModernTheme.ACCENT);
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel dayLabel = new JLabel(String.valueOf(e.getDateTime().getDayOfMonth()));
        dayLabel.setFont(ModernTheme.FONT_HEADER);
        dayLabel.setForeground(ModernTheme.TEXT_LIGHT);
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);

        datePanel.add(monthLabel);
        datePanel.add(dayLabel);
        card.add(datePanel, BorderLayout.WEST);

        // Info (Center)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(e.getName());
        nameLabel.setFont(ModernTheme.FONT_BOLD);
        nameLabel.setForeground(ModernTheme.TEXT_LIGHT);
        infoPanel.add(nameLabel);

        JLabel locLabel = new JLabel("📍 " + e.getLocation());
        locLabel.setFont(ModernTheme.FONT_SMALL);
        locLabel.setForeground(ModernTheme.TEXT_DARK);
        infoPanel.add(locLabel);

        JLabel timeLabel = new JLabel("🕒 " + e.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setFont(ModernTheme.FONT_SMALL);
        timeLabel.setForeground(ModernTheme.TEXT_DARK);
        infoPanel.add(timeLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Desc (Bottom)
        JLabel descLabel = new JLabel(e.getDescription());
        descLabel.setFont(ModernTheme.FONT_REGULAR);
        descLabel.setForeground(ModernTheme.TEXT_LIGHT);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private void postEvent() {
        String name = JOptionPane.showInputDialog(this, "Event Name:");
        if (name != null && !name.trim().isEmpty()) {
            String desc = JOptionPane.showInputDialog(this, "Description:");
            String loc = JOptionPane.showInputDialog(this, "Location:");

            // Simplified date input for prototype
            String daysStr = JOptionPane.showInputDialog(this, "Days from now (e.g. 1, 2):");
            int days = 1;
            try {
                days = Integer.parseInt(daysStr);
            } catch (Exception ex) {
            }

            Event e = new Event(
                    "E" + System.currentTimeMillis(),
                    name,
                    desc != null ? desc : "",
                    LocalDateTime.now().plusDays(days),
                    loc != null ? loc : "TBD",
                    DataManager.getInstance().getCurrentUser().getId());

            DataManager.getInstance().addEvent(e);
            refresh();
        }
    }
}
