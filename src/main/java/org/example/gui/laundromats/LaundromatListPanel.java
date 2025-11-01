package org.example.gui.laundromats;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.example.gui.utils.creators.roundedBorder;

public class LaundromatListPanel extends JPanel {

    private final JPanel listContainer;
    private JScrollBar verticalBar;
    private JPanel borderedWrapper;

    public LaundromatListPanel(Consumer<LaundromatData> onSelect) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(320, 0));
        setBackground(UIManager.getColor("Panel.background"));

        // === Outer wrapper panel ===
        borderedWrapper = new JPanel(new BorderLayout());
        borderedWrapper.setOpaque(false);

        // === Create scrollable list ===
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        listContainer.setBorder(new EmptyBorder(0, 0, 0, 8));

        JScrollPane scrollPane = new JScrollPane(listContainer,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        verticalBar = scrollPane.getVerticalScrollBar();

        borderedWrapper.add(scrollPane, BorderLayout.CENTER);
        add(borderedWrapper, BorderLayout.CENTER);

        applyThemeStyling();

        // === Demo/sample items ===
        List<LaundromatData> demo = Arrays.asList(
            new LaundromatData("WashEat Laundry", "209 Daang Maharlika Hwy, Poblacion District, Davao City",
                    "1.2 km", "2–4 days", "Pictures/washeat.png"),
            new LaundromatData("La Vahh Laundromat", "95 Emilio Jacinto St., Poblacion District, Davao City",
                    "850 m", "1–2 days", "Pictures/lavahh.png"),
            new LaundromatData("Allklean Laundromat", "95 Emilio Jacinto St., Poblacion District, Davao City",
                    "2.5 km", "3–5 days", "Pictures/allklean.png"),
            new LaundromatData("D'Laundry Station", "Bonifacio St., Poblacion District, Davao City",
                    "5.0 km", "3–6 days", "Pictures/dlaundry.png"),           
            new LaundromatData("RouTine Laundromat Roxas", "Aurora Quezon St, Poblacion District, Davao City",
                    "5.0 km", "3–6 days", "Pictures/routineRoxas.png")
                
        );

        for (LaundromatData data : demo) {
            LaundromatCard card = new LaundromatCard(data, onSelect);
            Dimension preferred = card.getPreferredSize();
            card.setPreferredSize(new Dimension(preferred.width, preferred.height - 10));
            listContainer.add(card);
            listContainer.add(Box.createVerticalStrut(12));
        }

        // === Watch for theme (LookAndFeel) changes ===
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName())) {
                    SwingUtilities.invokeLater(() -> applyThemeStyling());
                }
            }
        });
    }

    private void applyThemeStyling() {
        boolean dark = isDarkTheme();

        // Border: white in dark mode, blue in light mode
        Color borderColor = dark
                ? Color.WHITE
                : getLightModeBlue();

        borderedWrapper.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, borderColor, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Scrollbar: use theme-appropriate accent
        Color scrollbarColor = dark
                ? UIManager.getColor("ScrollBar.thumbDarkShadow") != null
                    ? UIManager.getColor("ScrollBar.thumbDarkShadow")
                    : new Color(0x4A90E2) // use your existing dark blue
                : getLightModeBlue();

        customizeScrollbar(verticalBar, scrollbarColor);

        revalidate();
        repaint();
    }

    private Color getLightModeBlue() {
        Color c = UIManager.getColor("Component.accentColor");
        if (c == null) c = UIManager.getColor("Actions.Blue");
        if (c == null) c = new Color(0x2196F3);
        return c;
    }

    private boolean isDarkTheme() {
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) return false;
        double luminance = (0.299 * bg.getRed()) + (0.587 * bg.getGreen()) + (0.114 * bg.getBlue());
        return luminance < 128;
    }

    private void customizeScrollbar(JScrollBar bar, Color accentColor) {
        bar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = accentColor;
                this.trackColor = UIManager.getColor("Panel.background");
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                int arc = 10;
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);
                g2.dispose();
            }
        });
    }
}
