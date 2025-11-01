package org.example.gui.laundromats;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Review card styled similarly to the laundromat cards:
 * - Rounded border with theme-aware accent color (light blue in light mode, white in dark mode)
 * - Header row with user icon (top-left) and username to its right (vertically centered)
 * - Horizontal divider (2px) directly under the header
 * - Comment text below, wrapped and non-editable
 *
 * UX/layout tweaks:
 * - 20px gap between cards is handled by the parent list (Details panel)
 * - Header (icon+name) moved slightly up and to the right (via inner padding)
 * - Divider placed closer to the header; divider and comment align with the icon's left edge
 * - Prevent trailing punctuation clipping by adding a small right inset (no special characters)
 */
public class ReviewCard extends roundedPanel {

    private final JLabel userIcon;
    private final JLabel userName;
    private final JTextArea commentArea;
    private final JSeparator headerDivider;

    public ReviewCard(String name, String comment) {
        super(18);

        setOpaque(true);
        setBackground(UIManager.getColor("background"));
        // Slightly up and to the right, generous right/bottom padding
        setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, getAccentBorderColor(), 2),
                new EmptyBorder(8, 24, 16, 16)  // top, left, bottom, right
        ));
        setLayout(new BorderLayout());
        setAlignmentX(LEFT_ALIGNMENT);

        // Let BoxLayout in parent stretch width; we won't overflow the viewport
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Vertical stack: header -> divider -> comment
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setAlignmentX(LEFT_ALIGNMENT);
        add(body, BorderLayout.CENTER);

        // Header row: use GridBag for precise alignment and padding
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints h = new GridBagConstraints();
        h.gridy = 0;

        userIcon = new JLabel(iconCreator.getIcon("Icons/lightmode/userIconBlue.svg", 24, 24));
        userIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        h.gridx = 0;
        h.weightx = 0;
        h.anchor = GridBagConstraints.WEST;
        h.insets = new Insets(0, 0, 0, 0);
        header.add(userIcon, h);

        userName = new JLabel(name != null ? name : "");
        userName.setAlignmentY(Component.CENTER_ALIGNMENT);
        // Bold default font, if provided by LAF
        userName.setFont(UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont").deriveFont(Font.BOLD)
                : header.getFont().deriveFont(Font.BOLD, header.getFont().getSize2D()));
        // Add a tiny right border to prevent last glyph clipping on some LAFs
        userName.setBorder(new EmptyBorder(0, 0, 0, 3));

        h.gridx = 1;
        h.weightx = 1.0; // consume remaining space
        h.anchor = GridBagConstraints.WEST;
        h.insets = new Insets(0, 8, 0, 0); // small gap between icon and name
        header.add(userName, h);

        body.add(header);

        // Divider: 2px, aligned with icon left edge
        body.add(Box.createVerticalStrut(4)); // tight above divider
        headerDivider = new JSeparator(SwingConstants.HORIZONTAL);
        headerDivider.setOpaque(true);
        Color accent = getAccentBorderColor();
        headerDivider.setForeground(accent);
        headerDivider.setBackground(accent);
        headerDivider.setPreferredSize(new Dimension(1, 2));
        headerDivider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        headerDivider.setAlignmentX(LEFT_ALIGNMENT);
        body.add(headerDivider);

        // Space between divider and comment
        body.add(Box.createVerticalStrut(12));

        // Comment: aligned with divider and icon
        commentArea = new JTextArea(comment != null ? comment : "");
        commentArea.setWrapStyleWord(true);
        commentArea.setLineWrap(true);
        commentArea.setEditable(false);
        commentArea.setOpaque(false);
        commentArea.setBorder(BorderFactory.createEmptyBorder());
        commentArea.setFont(UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont")
                : new Font("Dialog", Font.PLAIN, 12));
        commentArea.setAlignmentX(LEFT_ALIGNMENT);
        body.add(commentArea);

        applyThemeStyling();
        applyTextColors();
    }

    // Ensure width stretching works even if some LAF resets maximums
    @Override
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        return new Dimension(Integer.MAX_VALUE, d.height);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> {
            applyThemeStyling();
            applyTextColors();
        });
    }

    private void applyThemeStyling() {
        setBackground(UIManager.getColor("background"));
        setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, getAccentBorderColor(), 2),
                new EmptyBorder(8, 24, 16, 16)
        ));
        if (headerDivider != null) {
            Color accent = getAccentBorderColor();
            headerDivider.setForeground(accent);
            headerDivider.setBackground(accent);
        }
    }

    private void applyTextColors() {
        Color fg = UIManager.getColor("Label.foreground");
        if (fg == null) fg = Color.BLACK;

        if (userName != null) userName.setForeground(fg);
        if (commentArea != null) commentArea.setForeground(fg);
    }

    private static Color getAccentBorderColor() {
        return isDarkTheme() ? Color.WHITE : getLightModeBlue();
    }

    private static Color getLightModeBlue() {
        Color c = UIManager.getColor("Component.accentColor");
        if (c == null) c = UIManager.getColor("Actions.Blue");
        if (c == null) c = new Color(0x2196F3);
        return c;
    }

    private static boolean isDarkTheme() {
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) return false;
        double luminance = (0.299 * bg.getRed()) + (0.587 * bg.getGreen()) + (0.114 * bg.getBlue());
        return luminance < 128;
    }
}