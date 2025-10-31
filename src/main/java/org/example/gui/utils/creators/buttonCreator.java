package org.example.gui.utils.creators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class buttonCreator extends roundedPanel {
    private final JLabel label;
    private final String fontKey;
    private boolean selected = false;

    // Optional persistent font overrides for the inner label
    private Float fontSizeDelta = null;
    private Font customFont = null;

    // Auto-scale-to-width support
    private boolean autoScale = false;
    private float autoScaleRatio = 0.90f; // target: text width ~ 90% of button width
    private float autoMinSize = 10f;
    private float autoMaxSize = 28f;

    public buttonCreator(String text, String fontKey, Runnable action) {
        this.fontKey = fontKey;

        setLayout(new GridBagLayout());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        setPreferredSize(new Dimension(150, 40));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        label = new JLabel(text, SwingConstants.CENTER);
        add(label);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!selected) {
                    Color hover = UIManager.getColor("Button.hoverBackground");
                    if (hover != null) setBackground(hover);
                    repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!selected) {
                    Color bg = UIManager.getColor("Button.background");
                    if (bg != null) setBackground(bg);
                    repaint();
                }
            }
        });

        // Re-scale when the button size changes
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (autoScale) rescaleLabelFont();
            }
        });

        updateUI();
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if (label != null) {
            // Start from custom font if provided, otherwise from UIManager
            Font f = (customFont != null) ? customFont : UIManager.getFont(fontKey);
            if (f != null && fontSizeDelta != null && !autoScale) {
                // Only apply size delta when not auto-scaling
                float base = f.getSize2D();
                f = f.deriveFont(Math.max(1f, base + fontSizeDelta));
            }
            if (f != null) {
                label.setFont(f);
            }
            Color fg = UIManager.getColor("Button.foreground");
            if (fg != null) label.setForeground(fg);
        }

        if (!selected) {
            Color bg = UIManager.getColor("Button.background");
            if (bg != null) setBackground(bg);
        } else {
            Color pressed = UIManager.getColor("Button.pressedBackground");
            if (pressed != null) setBackground(pressed);
        }

        // Defer rescale to after LAF applies metrics
        if (autoScale) {
            SwingUtilities.invokeLater(this::rescaleLabelFont);
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateUI();
    }

    public boolean isSelected() {
        return selected;
    }

    // Slight font-size bump that persists across LAF updates (ignored when autoScale is enabled)
    public void setFontSizeDelta(float delta) {
        this.fontSizeDelta = delta;
        updateUI();
    }

    public Float getFontSizeDelta() {
        return fontSizeDelta;
    }

    // Set a custom font for the label that persists across LAF updates
    public void setCustomFont(Font font) {
        this.customFont = font;
        updateUI();
    }

    public Font getCustomFont() {
        return customFont;
    }

    // Enable auto-scaling of the label font to occupy ~ratio of the button width
    public void enableAutoScaleToWidth(float ratio, float minSize, float maxSize) {
        this.autoScale = true;
        this.autoScaleRatio = Math.max(0.1f, Math.min(1.0f, ratio));
        this.autoMinSize = Math.max(1f, minSize);
        this.autoMaxSize = Math.max(this.autoMinSize, maxSize);
        // When autoScale is on, ignore manual size delta
        this.fontSizeDelta = null;
        rescaleLabelFont();
    }

    // Manually trigger a rescale (optional)
    public void rescaleNow() {
        if (autoScale) rescaleLabelFont();
    }

    // Core logic to find a font size that fits width (~ratio) and height constraints
    private void rescaleLabelFont() {
        if (label == null || label.getText() == null) return;
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Available space inside the button (subtract a small safety padding)
        int horizontalPadding = 16;
        int verticalPadding = 8;
        int targetTextWidth = Math.max(1, Math.round((w - horizontalPadding) * autoScaleRatio));
        int maxTextHeight = Math.max(1, h - verticalPadding);

        // Base font family/style: prefer custom, else current label font, else default
        Font base = (customFont != null) ? customFont : label.getFont();
        if (base == null) base = new Font("Dialog", Font.BOLD, 12);

        // Binary search the best size
        float lo = autoMinSize;
        float hi = autoMaxSize;
        float best = lo;
        for (int i = 0; i < 18; i++) { // iterate enough for precision
            float mid = (lo + hi) / 2f;
            Font test = base.deriveFont(mid);
            FontMetrics fm = label.getFontMetrics(test);
            int textW = fm.stringWidth(label.getText());
            int textH = fm.getAscent() + fm.getDescent();

            boolean fitsWidth = textW <= targetTextWidth;
            boolean fitsHeight = textH <= maxTextHeight;

            if (fitsWidth && fitsHeight) {
                best = mid;
                lo = mid + 0.25f; // try bigger
            } else {
                hi = mid - 0.25f; // try smaller
            }
            if (hi < lo) break;
        }

        Font chosen = base.deriveFont(best);
        if (!chosen.equals(label.getFont())) {
            label.setFont(chosen);
            revalidate();
            repaint();
        }
    }
}