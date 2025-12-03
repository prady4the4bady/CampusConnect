package com.campusconnect.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private Color hoverColor;
    private Color normalColor;
    private Color pressedColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        super(text);
        this.normalColor = ModernTheme.PRIMARY;
        this.hoverColor = ModernTheme.PRIMARY.brighter();
        this.pressedColor = ModernTheme.PRIMARY.darker();

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(ModernTheme.FONT_BOLD);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offset = isPressed ? 2 : 0;

        // Draw Shadow
        if (!isPressed) {
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(2, 2 + offset, getWidth() - 4, getHeight() - 4, 10, 10);
        }

        // Draw Button
        if (isPressed) {
            g2.setColor(pressedColor);
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        g2.fillRoundRect(0, offset, getWidth(), getHeight() - offset, 10, 10);

        // Draw Text
        FontMetrics fm = g2.getFontMetrics();
        Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
        int textX = (getWidth() - stringBounds.width) / 2;
        int textY = (getHeight() - offset - stringBounds.height) / 2 + fm.getAscent() + offset;

        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
