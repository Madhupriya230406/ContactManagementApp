package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Design system and styling constants for the Contact Management System GUI.
 * Implements a modern professional Blue and White color theme.
 */
public final class Theme {

    // --- Color Palette ---
    public static final Color SIDEBAR_BG = new Color(26, 54, 93);         // Deep Royal Navy Blue (#1A365D)
    public static final Color SIDEBAR_HOVER = new Color(43, 84, 142);     // Medium Navy Hover
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);    // Bright Blue Accent
    public static final Color SIDEBAR_TEXT = new Color(241, 245, 249);    // Crisp Off-White
    public static final Color SIDEBAR_MUTED = new Color(148, 163, 184);   // Slate Gray

    public static final Color APP_BG = new Color(248, 250, 252);          // Light Slate Canvas (#F8FAFC)
    public static final Color CARD_BG = Color.WHITE;                      // Pure White (#FFFFFF)
    public static final Color CARD_BORDER = new Color(226, 232, 240);     // Subtle Gray Border (#E2E8F0)

    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);      // Brand Blue (#2563EB)
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);     // Darker Blue (#1D4ED8)
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);    // Emerald Green (#10B981)
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color DANGER_RED = new Color(239, 68, 68);        // Rose Red (#EF4444)
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color SECONDARY_GRAY = new Color(100, 116, 139);  // Slate Gray (#64748B)

    public static final Color TEXT_DARK = new Color(15, 23, 42);          // Dark Charcoal (#0F172A)
    public static final Color TEXT_MUTED = new Color(100, 116, 139);      // Muted Slate (#64748B)
    public static final Color TABLE_HEADER_BG = new Color(241, 245, 249); // Table Header Gray (#F1F5F9)
    public static final Color TABLE_STRIPE = new Color(248, 250, 252);    // Alternating Row Color

    // --- Fonts ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT_NUM = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 11);

    private Theme() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a styled modern button with rounded corners and hover effects.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(brighterColor(bgColor));
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(FONT_BOLD);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Creates a styled text field with rounded borders and padding.
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /**
     * Creates a white card panel with a subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        return card;
    }

    private static Color brighterColor(Color c) {
        int r = Math.min(255, c.getRed() + 20);
        int g = Math.min(255, c.getGreen() + 20);
        int b = Math.min(255, c.getBlue() + 20);
        return new Color(r, g, b);
    }
}
