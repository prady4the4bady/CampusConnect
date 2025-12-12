package com.campusconnect.gui.components;

import java.awt.Color;
import java.awt.Font;

public class ModernTheme {
    
    public static final Color PRIMARY = new Color(64, 123, 255); 
    public static final Color ACCENT = new Color(255, 171, 0); 
    public static final Color BG_DARK = new Color(18, 18, 18); 
    public static final Color BG_LIGHT = new Color(30, 30, 30); 
    public static final Color TEXT_LIGHT = new Color(240, 240, 240);
    public static final Color TEXT_DARK = new Color(180, 180, 180); 
    public static final Color PANEL_BG = new Color(30, 30, 30); 
    public static final Color INPUT_BG = new Color(45, 45, 45);
    public static final Color BORDER = new Color(60, 60, 60);

    
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    
    public static void styleModernTabs(javax.swing.JTabbedPane tabbedPane) {
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_LIGHT);
        tabbedPane.setFont(FONT_BOLD);
        tabbedPane.setBorder(null);

        
        tabbedPane.setOpaque(true);
        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.TOP);

        
        if (tabbedPane.getComponentCount() > 0) {
            for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
                java.awt.Component comp = tabbedPane.getComponent(i);
                if (comp instanceof javax.swing.JScrollPane) {
                    javax.swing.JScrollPane sp = (javax.swing.JScrollPane) comp;
                    sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());
                    sp.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
                }
            }
        }
    }

    public static void applyModernScrollbar(javax.swing.JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBorder(null);
    }
}
