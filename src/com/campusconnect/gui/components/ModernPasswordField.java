package com.campusconnect.gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernPasswordField extends JPasswordField {
    public ModernPasswordField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setFont(ModernTheme.FONT_REGULAR);
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        super.paintComponent(g);
        g2.dispose();
    }
}
