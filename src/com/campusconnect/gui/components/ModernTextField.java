package com.campusconnect.gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernTextField extends JTextField {
    private boolean isFocused = false;

    public ModernTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 10, 8, 10)); // Increased padding
        setFont(ModernTheme.FONT_REGULAR);
        setForeground(Color.WHITE);
        setCaretColor(ModernTheme.ACCENT);
        setBackground(ModernTheme.INPUT_BG);

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                isFocused = true;
                repaint();
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Border
        if (isFocused) {
            g2.setColor(ModernTheme.PRIMARY);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(ModernTheme.BORDER);
            g2.setStroke(new BasicStroke(1));
        }
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

        super.paintComponent(g);
        g2.dispose();
    }
}
