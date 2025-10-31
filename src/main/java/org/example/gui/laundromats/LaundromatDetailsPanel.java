package org.example.gui.laundromats;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.buttonCreator;

public class LaundromatDetailsPanel extends JPanel {

    private JPanel headerPanel;
    private JPanel topInfoRow;
    private JTextArea descriptionArea;
    private JPanel reviewsPanel;
    private JPanel servicesPanel;
    private buttonCreator pickupBtn;

    // labels
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel distanceLabel;
    private JLabel deliveryLabel;
    private JLabel ratingLabel;
    private JLabel logoLabel;
    private JTextArea addressArea;

    private final JPanel placeholderWrapper;
    private final JLabel placeholderIconLabel;

    // left/right inner cards
    private roundedPanel descriptionPanel;
    private roundedPanel reviewsWrapperPanel;

    // center container (Left | 40px fixed gap | Right)
    private JPanel centerContentPanel;

    // Fixed height for the pickup button (+20 over default)
    private int pickupBtnFixedHeight = -1;

    public LaundromatDetailsPanel() {
        // 20 (layout) + 20 (inner top padding) = 40px visual gap below header
        setLayout(new BorderLayout(0, 20));
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- HEADER PANEL (rounded) ---
        headerPanel = new roundedPanel(18);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(UIManager.getColor("background"));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, UIManager.getColor("listBorder"), 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Fonts
        Font fredokaMedium16 = UIManager.getFont("Title.font") != null
                ? UIManager.getFont("Title.font").deriveFont(Font.BOLD, 16f)
                : new Font("Fredoka Medium", Font.BOLD, 16);
        Font fredoka16 = fredokaMedium16;
        Font lato9 = UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 9f)
                : new Font("Lato", Font.PLAIN, 9);

        // --- TOP INFO ROW ---
        topInfoRow = new JPanel(new GridBagLayout());
        topInfoRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        Color dividerColor = UIManager.getColor("listBorder");
        Color bg = UIManager.getColor("background");

        // LEFTMOST (logo)
        JPanel leftmostPanel = new JPanel(new GridBagLayout());
        leftmostPanel.setOpaque(true);
        leftmostPanel.setBackground(bg);
        leftmostPanel.setPreferredSize(new Dimension(80, 80));
        leftmostPanel.setMinimumSize(new Dimension(70, 60));
        logoLabel = new JLabel(iconCreator.getIcon("Icons/lightmode/laundromatLogo.svg", 48, 48));
        GridBagConstraints lg = new GridBagConstraints();
        lg.anchor = GridBagConstraints.CENTER;
        leftmostPanel.add(logoLabel, lg);
        leftmostPanel.setBorder(new EmptyBorder(0, 0, 0, 12));
        gbc.gridx = 0;
        gbc.weightx = 0;
        topInfoRow.add(leftmostPanel, gbc);

        // INNER LEFT (name + address)
        JPanel innerLeftPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        innerLeftPanel.setOpaque(true);
        innerLeftPanel.setBackground(bg);
        innerLeftPanel.setBorder(new EmptyBorder(8, 8, 8, 2));
        innerLeftPanel.setPreferredSize(new Dimension(420, 80));
        innerLeftPanel.setMinimumSize(new Dimension(160, 60));

        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        upperPanel.setOpaque(false);
        nameLabel = new JLabel("Laundromat name");
        nameLabel.setFont(fredoka16);
        nameLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
        upperPanel.add(nameLabel);

        JPanel lowerPanel = new JPanel(new BorderLayout(6, 0));
        lowerPanel.setOpaque(false);
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        JLabel addrIcon = new JLabel(iconCreator.getIcon("Icons/address.svg", 12, 12));
        iconPanel.add(addrIcon);

        addressArea = new JTextArea("Address goes here");
        addressArea.setFont(lato9);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setEditable(false);
        addressArea.setOpaque(false);
        addressArea.setBorder(new EmptyBorder(2, 0, 0, 0));
        addressArea.setForeground(UIManager.getColor("Label.foreground"));
        addressArea.setAlignmentY(JTextArea.TOP_ALIGNMENT);
        addressArea.setRows(2);
        FontMetrics fm = addressArea.getFontMetrics(lato9);
        int lineHeight = fm.getHeight();
        addressArea.setPreferredSize(new Dimension(0, lineHeight * 2 + 4));

        addressLabel = new JLabel();
        addressLabel.setFont(lato9);

        lowerPanel.add(iconPanel, BorderLayout.WEST);
        lowerPanel.add(addressArea, BorderLayout.CENTER);

        innerLeftPanel.add(upperPanel);
        innerLeftPanel.add(lowerPanel);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        topInfoRow.add(innerLeftPanel, gbc);

        // DIVIDER
        JPanel dividerWrapper = new JPanel(new GridBagLayout());
        dividerWrapper.setOpaque(false);
        JSeparator verticalSep = new JSeparator(SwingConstants.VERTICAL);
        verticalSep.setForeground(dividerColor);
        verticalSep.setBackground(dividerColor);
        verticalSep.setOpaque(true);
        verticalSep.setPreferredSize(new Dimension(1, 60));
        dividerWrapper.add(verticalSep);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 12, 0, 12);
        topInfoRow.add(dividerWrapper, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);

        // INNER RIGHT (distance + delivery)
        JPanel innerRightPanel = new JPanel();
        innerRightPanel.setLayout(new BoxLayout(innerRightPanel, BoxLayout.Y_AXIS));
        innerRightPanel.setOpaque(true);
        innerRightPanel.setBackground(bg);
        innerRightPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        innerRightPanel.setPreferredSize(new Dimension(160, 80));
        innerRightPanel.setMinimumSize(new Dimension(100, 60));

        JPanel distanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        distanceRow.setOpaque(false);
        JLabel distIcon = new JLabel(iconCreator.getIcon("Icons/distancefromUser.svg", 12, 12));
        distanceLabel = new JLabel("0.0 km");
        distanceLabel.setFont(lato9);
        distanceRow.add(distIcon);
        distanceRow.add(distanceLabel);

        JPanel deliveryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        deliveryRow.setOpaque(false);
        JLabel delIcon = new JLabel(iconCreator.getIcon("Icons/deliveryperiod.svg", 12, 12));
        deliveryLabel = new JLabel("1–2 days");
        deliveryLabel.setFont(lato9);
        deliveryRow.add(delIcon);
        deliveryRow.add(deliveryLabel);

        innerRightPanel.add(distanceRow);
        innerRightPanel.add(Box.createVerticalStrut(8));
        innerRightPanel.add(deliveryRow);

        gbc.gridx = 3;
        gbc.weightx = 0;
        topInfoRow.add(innerRightPanel, gbc);

        // RIGHTMOST (rating)
        JPanel rightmostPanel = new JPanel(new GridBagLayout());
        rightmostPanel.setOpaque(true);
        rightmostPanel.setBackground(bg);
        rightmostPanel.setPreferredSize(new Dimension(120, 80));
        rightmostPanel.setMinimumSize(new Dimension(80, 60));
        GridBagConstraints rg = new GridBagConstraints();
        rg.gridx = 0;
        rg.gridy = 0;
        rg.anchor = GridBagConstraints.EAST;
        rg.insets = new Insets(0, 0, 0, 8);
        JPanel starRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        starRow.setOpaque(false);
        JLabel starIcon = new JLabel(iconCreator.getIcon("Icons/lightmode/star.svg", 18, 18));
        ratingLabel = new JLabel("0.0");
        ratingLabel.setFont(fredoka16);
        starRow.add(starIcon);
        starRow.add(ratingLabel);
        rightmostPanel.add(starRow, rg);

        gbc.gridx = 4;
        gbc.weightx = 0;
        topInfoRow.add(rightmostPanel, gbc);

        headerPanel.add(topInfoRow, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Build center content once: Left | fixed 40px gap | Right
        buildCenterContent();

        // === PLACEHOLDER STATE ===
        placeholderWrapper = new JPanel(new GridBagLayout());
        placeholderWrapper.setOpaque(false);
        placeholderIconLabel = new JLabel(iconCreator.getIcon("Icons/darkmode/laundromatLogoDarkMode.svg", 250, 250));
        placeholderWrapper.add(placeholderIconLabel, new GridBagConstraints());

        // Start with placeholder only
        remove(headerPanel);
        remove(centerContentPanel);
        add(placeholderWrapper, BorderLayout.CENTER);
    }

    private void buildCenterContent() {
        // Reusable content
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);

        reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setOpaque(false);

        // Services: full-width, fixed 3 columns, no outer padding
        servicesPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        servicesPanel.setOpaque(false);
        servicesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        Color bg = UIManager.getColor("background");
        Color borderColor = UIManager.getColor("listBorder");

        // Constant 60/40 horizontal split and fixed 40px gap.
        centerContentPanel = new JPanel(new TwoColumnFixedGapLayout(0.60, 40));
        centerContentPanel.setOpaque(false);

        // OUTER LEFT: 70/30 vertical split
        JPanel outerLeftPanel = new JPanel(new GridBagLayout()) {
            @Override public Dimension getMinimumSize() { return new Dimension(0, super.getMinimumSize().height); }
        };
        outerLeftPanel.setOpaque(false);
        outerLeftPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        GridBagConstraints ol = new GridBagConstraints();
        ol.gridx = 0;
        ol.fill = GridBagConstraints.BOTH;
        ol.weightx = 1.0;

        // Description card (top-left) — 70% height share
        descriptionPanel = new roundedPanel(18);
        descriptionPanel.setOpaque(true);
        descriptionPanel.setBackground(bg);
        descriptionPanel.setLayout(new BorderLayout(0, 10));
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, borderColor, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel descTitle = new JLabel("What We Offer:");
        descTitle.setFont(UIManager.getFont("defaultFont"));
        descriptionPanel.add(descTitle, BorderLayout.NORTH);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        descriptionPanel.add(descScroll, BorderLayout.CENTER);

        ol.gridy = 0;
        ol.weighty = 0.70;
        outerLeftPanel.add(descriptionPanel, ol);

        // Spacer between description and services (20px)
        ol.gridy = 1;
        ol.weighty = 0;
        ol.insets = new Insets(20, 0, 0, 0);
        outerLeftPanel.add(Box.createVerticalStrut(20), ol);
        ol.insets = new Insets(0, 0, 0, 0);

        // Services container (bottom-left) — 30% height share, full width
        ol.gridy = 2;
        ol.weighty = 0.30;
        outerLeftPanel.add(servicesPanel, ol);

        // OUTER RIGHT: reviews + fixed-height full-width button with 40px gap
        JPanel outerRightPanel = new JPanel(new VerticalTopFillBottomFixedGapLayout(40)) {
            @Override public Dimension getMinimumSize() { return new Dimension(0, super.getMinimumSize().height); }
        };
        outerRightPanel.setOpaque(false);
        outerRightPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Reviews card
        reviewsWrapperPanel = new roundedPanel(18);
        reviewsWrapperPanel.setOpaque(true);
        reviewsWrapperPanel.setBackground(bg);
        reviewsWrapperPanel.setLayout(new BorderLayout(0, 10));
        reviewsWrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, borderColor, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel revTitle = new JLabel("Reviews:");
        revTitle.setFont(UIManager.getFont("defaultFont"));
        reviewsWrapperPanel.add(revTitle, BorderLayout.NORTH);
        JScrollPane revScroll = new JScrollPane(reviewsPanel);
        revScroll.setBorder(BorderFactory.createEmptyBorder());
        revScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        reviewsWrapperPanel.add(revScroll, BorderLayout.CENTER);

        // Request pickup button (no wrapper; full width, fixed height = default + 20)
        pickupBtn = new buttonCreator("Request Pickup", "Button.font", () -> {
            Container parent = this;
            while (parent != null && !(parent.getLayout() instanceof CardLayout)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                CardLayout cl = (CardLayout) parent.getLayout();
                cl.show(parent, "PICKUP");
            }
        });

        // Set font to Fredoka Bold; auto-scale to take ~90% of button width
        Font base = UIManager.getFont("Button.font");
        float baseSize = base != null ? base.getSize2D() : 13f;
        Font fredokaBold = getFredokaBold(baseSize, base != null ? base.deriveFont(Font.BOLD) : new Font("Dialog", Font.BOLD, Math.round(baseSize)));
        pickupBtn.setCustomFont(fredokaBold);
        // Enable auto-scaling (ratio ~0.90) with sensible min/max; height constraint is enforced internally
        pickupBtn.enableAutoScaleToWidth(0.90f, 12f, 26f);

        // Compute fixed height and width constraints after font setup
        Dimension ph = pickupBtn.getPreferredSize();
        pickupBtnFixedHeight = Math.max(ph.height + 20, ph.height);
        pickupBtn.setPreferredSize(new Dimension(ph.width, pickupBtnFixedHeight));
        pickupBtn.setMinimumSize(new Dimension(0, pickupBtnFixedHeight));
        pickupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, pickupBtnFixedHeight));

        // Assemble right column
        outerRightPanel.add(reviewsWrapperPanel, VerticalTopFillBottomFixedGapLayout.TOP);
        outerRightPanel.add(pickupBtn, VerticalTopFillBottomFixedGapLayout.BOTTOM);

        // Add columns to center
        centerContentPanel.add(outerLeftPanel, TwoColumnFixedGapLayout.LEFT);
        centerContentPanel.add(outerRightPanel, TwoColumnFixedGapLayout.RIGHT);

        add(centerContentPanel, BorderLayout.CENTER);
    }

    // Try to get a Fredoka bold font; fallback to provided font if unavailable
    private static Font getFredokaBold(float size, Font fallback) {
        String[] candidates = { "Fredoka", "Fredoka Medium", "Fredoka One" };
        for (String name : candidates) {
            Font f = new Font(name, Font.BOLD, Math.round(size));
            if (!"Dialog".equalsIgnoreCase(f.getFamily())) {
                return f.deriveFont(Font.BOLD, size);
            }
        }
        return fallback.deriveFont(Font.BOLD, size);
    }

    /** Populate UI when a laundromat is clicked. */
    public void setLaundromat(LaundromatData data) {
        if (placeholderWrapper.getParent() != null) {
            remove(placeholderWrapper);
            add(headerPanel, BorderLayout.NORTH);
            add(centerContentPanel, BorderLayout.CENTER);
        }

        // update fields
        logoLabel.setIcon(iconCreator.getIcon("Icons/lightmode/laundromatLogo.svg", 48, 48));
        nameLabel.setText(data.name);
        addressArea.setText(data.address);
        distanceLabel.setText(data.distance);
        deliveryLabel.setText(data.deliveryPeriod);
        ratingLabel.setText(String.format("%.1f", data.stars > 0 ? data.stars : 0.0));

        descriptionArea.setText(data.description != null ? data.description : "");

        // Populate reviews
        reviewsPanel.removeAll();
        Arrays.asList(
                new ReviewCard("John Doe", "Great service! Will definitely come back."),
                new ReviewCard("Jane Doe", "Fast delivery and very clean laundry."),
                new ReviewCard("Alex", "Convenient and affordable.")
        ).forEach(reviewsPanel::add);

        // Populate services: each one gets its own rounded border card, no inner padding
        servicesPanel.removeAll();
        servicesPanel.add(createServiceItem("Wash & Fold", "Icons/Services/washandFold.svg"));
        servicesPanel.add(createServiceItem("Dry Clean", "Icons/Services/dryClean.svg"));
        servicesPanel.add(createServiceItem("Ironing", "Icons/Services/iron.svg"));

        // Ensure button rescales after content/layout changes
        SwingUtilities.invokeLater(() -> {
            if (pickupBtn != null) pickupBtn.rescaleNow();
        });

        revalidate();
        repaint();
    }

    // Create a single service item with its own rounded border, no inner padding; fills its grid cell
    private JComponent createServiceItem(String title, String iconPath) {
        Color bg = UIManager.getColor("background");
        Color borderColor = UIManager.getColor("listBorder");

        // Actual service content (transparent ServiceCard)
        JComponent content = new ServiceCard(title, iconPath);

        // Wrap the content in its own rounded card, NO inner padding
        roundedPanel card = new roundedPanel(18);
        card.setOpaque(true);
        card.setBackground(bg);
        card.setLayout(new BorderLayout());
        card.setBorder(new roundedBorder(18, borderColor, 2)); // no EmptyBorder padding
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.background"));

        if (headerPanel != null) headerPanel.setBackground(UIManager.getColor("background"));

        Color bg = UIManager.getColor("background");
        if (descriptionPanel != null) descriptionPanel.setBackground(bg);
        if (reviewsWrapperPanel != null) reviewsWrapperPanel.setBackground(bg);

        if (pickupBtn != null) {
            // Keep height; text width scaling handled inside buttonCreator
            if (pickupBtnFixedHeight > 0) {
                Dimension ph = pickupBtn.getPreferredSize();
                pickupBtn.setPreferredSize(new Dimension(ph.width, pickupBtnFixedHeight));
                pickupBtn.setMinimumSize(new Dimension(0, pickupBtnFixedHeight));
                pickupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, pickupBtnFixedHeight));
            }
            SwingUtilities.invokeLater(pickupBtn::rescaleNow);
        }

        Font fredokaMedium16 = UIManager.getFont("Title.font") != null
                ? UIManager.getFont("Title.font").deriveFont(Font.BOLD, 16f)
                : new Font("Fredoka Medium", Font.BOLD, 16);
        Font lato9 = UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 9f)
                : new Font("Lato", Font.PLAIN, 9);

        if (nameLabel != null) nameLabel.setFont(fredokaMedium16);
        if (ratingLabel != null) ratingLabel.setFont(fredokaMedium16);
        if (addressLabel != null) addressLabel.setFont(lato9);
        if (distanceLabel != null) distanceLabel.setFont(lato9);
        if (deliveryLabel != null) deliveryLabel.setFont(lato9);

        Color dividerColor = UIManager.getColor("listBorder");
        if (topInfoRow != null) {
            for (Component c : topInfoRow.getComponents()) {
                if (c instanceof JPanel) {
                    for (Component inner : ((JPanel) c).getComponents()) {
                        if (inner instanceof JSeparator) {
                            inner.setForeground(dividerColor);
                            inner.setBackground(dividerColor);
                        }
                    }
                }
            }
        }

        revalidate();
        repaint();
    }

    // --- Custom Layout: fixed gap and constant ratio left/right, aligned to panel edges ---
    private static class TwoColumnFixedGapLayout implements LayoutManager2 {
        static final String LEFT = "LEFT";
        static final String RIGHT = "RIGHT";

        private final double leftRatio; // 0.60 for 60/40
        private final int gap;          // 40px

        private Component leftComp;
        private Component rightComp;

        TwoColumnFixedGapLayout(double leftRatio, int gap) {
            this.leftRatio = Math.max(0.0, Math.min(1.0, leftRatio));
            this.gap = Math.max(0, gap);
        }

        @Override public void addLayoutComponent(Component comp, Object constraints) {
            if (LEFT.equals(constraints)) {
                leftComp = comp;
            } else if (RIGHT.equals(constraints)) {
                rightComp = comp;
            } else {
                if (leftComp == null) leftComp = comp;
                else rightComp = comp;
            }
        }
        @Override public void addLayoutComponent(String name, Component comp) { addLayoutComponent(comp, name); }
        @Override public void removeLayoutComponent(Component comp) {
            if (comp == leftComp) leftComp = null;
            else if (comp == rightComp) rightComp = null;
        }

        @Override public Dimension preferredLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            Dimension lp = leftComp != null ? leftComp.getPreferredSize() : new Dimension(0,0);
            Dimension rp = rightComp != null ? rightComp.getPreferredSize() : new Dimension(0,0);
            int w = in.left + lp.width + gap + rp.width + in.right;
            int h = in.top + Math.max(lp.height, rp.height) + in.bottom;
            return new Dimension(w, h);
        }
        @Override public Dimension minimumLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            int w = in.left + gap + in.right;
            int h = in.top + in.bottom;
            return new Dimension(w, h);
        }
        @Override public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        @Override public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int x = in.left;
            int y = in.top;
            int availW = Math.max(0, parent.getWidth() - in.left - in.right);
            int availH = Math.max(0, parent.getHeight() - in.top - in.bottom);

            int contentW = Math.max(0, availW - gap);
            int leftW = (int) Math.round(contentW * leftRatio);
            int rightW = contentW - leftW;

            if (leftComp != null) leftComp.setBounds(x, y, leftW, availH);
            if (rightComp != null) rightComp.setBounds(x + leftW + gap, y, rightW, availH);
        }
        @Override public float getLayoutAlignmentX(Container target) { return 0.0f; }
        @Override public float getLayoutAlignmentY(Container target) { return 0.0f; }
        @Override public void invalidateLayout(Container target) { }
    }

    // --- Custom vertical layout for right column (reviews fill, button fixed height) ---
    private static class VerticalTopFillBottomFixedGapLayout implements LayoutManager2 {
        static final String TOP = "TOP";
        static final String BOTTOM = "BOTTOM";

        private final int gap;
        private Component topComp;
        private Component bottomComp;

        VerticalTopFillBottomFixedGapLayout(int gap) { this.gap = Math.max(0, gap); }

        @Override public void addLayoutComponent(Component comp, Object constraints) {
            if (TOP.equals(constraints)) topComp = comp;
            else if (BOTTOM.equals(constraints)) bottomComp = comp;
            else { if (topComp == null) topComp = comp; else bottomComp = comp; }
        }
        @Override public void addLayoutComponent(String name, Component comp) { addLayoutComponent(comp, name); }
        @Override public void removeLayoutComponent(Component comp) {
            if (comp == topComp) topComp = null;
            else if (comp == bottomComp) bottomComp = null;
        }

        @Override public Dimension preferredLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            Dimension tp = topComp != null ? topComp.getPreferredSize() : new Dimension(0,0);
            Dimension bp = bottomComp != null ? bottomComp.getPreferredSize() : new Dimension(0,0);
            int w = in.left + Math.max(tp.width, bp.width) + in.right;
            int h = in.top + tp.height + (bottomComp != null ? gap + bp.height : 0) + in.bottom;
            return new Dimension(w, h);
        }
        @Override public Dimension minimumLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            int w = in.left + in.right;
            int h = in.top + (bottomComp != null ? bottomComp.getMinimumSize().height : 0) + in.bottom;
            return new Dimension(w, h);
        }
        @Override public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        @Override public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int x = in.left;
            int y = in.top;
            int availW = Math.max(0, parent.getWidth() - in.left - in.right);
            int availH = Math.max(0, parent.getHeight() - in.top - in.bottom);

            int bottomH = 0;
            if (bottomComp != null) {
                Dimension bp = bottomComp.getPreferredSize();
                bottomH = Math.min(bp.height, Math.max(0, availH));
            }
            int topH = Math.max(0, availH - (bottomComp != null ? (gap + bottomH) : 0));

            if (topComp != null) topComp.setBounds(x, y, availW, topH);
            if (bottomComp != null) bottomComp.setBounds(x, y + topH + gap, availW, bottomH);
        }
        @Override public float getLayoutAlignmentX(Container target) { return 0.0f; }
        @Override public float getLayoutAlignmentY(Container target) { return 0.0f; }
        @Override public void invalidateLayout(Container target) { }
    }
}