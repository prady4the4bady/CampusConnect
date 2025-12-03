package com.campusconnect.gui.components;

import javax.swing.*;
import java.awt.*;

public class ModernPanel extends JPanel {
    private int cornerRadius = 15;
    private Color backgroundColor;

    public ModernPanel() {
        this.backgroundColor = ModernTheme.PANEL_BG;
        setOpaque(false);
    }

    public ModernPanel(LayoutManager layout) {
        super(layout);
        this.backgroundColor = ModernTheme.PANEL_BG;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        super.paintComponent(g);
        g2.dispose();
    }
}
