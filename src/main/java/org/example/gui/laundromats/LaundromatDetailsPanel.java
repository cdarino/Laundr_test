package org.example.gui.laundromats;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.buttonCreator;

import org.example.session.AppState;
import org.example.database.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LaundromatDetailsPanel extends JPanel {

    // Header sizing
    private static final int HEADER_ROW_HEIGHT = 96;
    private static final int LOGO_ICON_SIZE   = 60;

    private JPanel headerPanel;
    private JPanel topInfoRow;
    private JTextArea descriptionArea;
    private JPanel reviewsPanel;
    private JPanel servicesPanel;
    private buttonCreator pickupBtn;

    // labels
    private JLabel nameLabel;
    private JLabel addressLabel; // kept for compatibility (unused)
    private JLabel distanceLabel;
    private JLabel deliveryLabel;
    private JLabel ratingLabel;
    private JLabel logoLabel;
    private EllipsisLabel addressEllipsisLabel;

    // ADDED: references to swap icons on theme change
    private JLabel addrIconLabel;
    private JLabel distIconLabel;
    private JLabel delIconLabel;
    private JLabel starIconLabel;

    // section titles (outside card borders)
    private JLabel highlightsTitleLabel;
    private JLabel servicesTitleLabel;
    private JLabel reviewsTitleLabel;

    private final JPanel placeholderWrapper;
    private final JLabel placeholderIconLabel;

    // left/right inner cards
    private roundedPanel descriptionPanel;    // Highlights panel
    private roundedPanel reviewsWrapperPanel;

    // divider reference (so we can recolor on theme switch)
    private JSeparator verticalSep;

    // center container (Left | fixed gap | Right)
    private JPanel centerContentPanel;

    // Keep reference to reviews scroll pane for styling and behavior
    private JScrollPane reviewsScrollPane;

    // Fixed height for the pickup button (+20 over default)
    private int pickupBtnFixedHeight = -1;

    public LaundromatDetailsPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- HEADER PANEL (rounded) ---
        headerPanel = new roundedPanel(18);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(UIManager.getColor("background"));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, getAccentBorderColor(), 2),
                new EmptyBorder(20, 24, 20, 24)
        ));

        // Fonts
        Font fredokaTitle = UIManager.getFont("Title.font") != null
                ? UIManager.getFont("Title.font").deriveFont(Font.BOLD)
                : new Font("Fredoka Medium", Font.BOLD, 16);
        Font latoBase = UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont")
                : new Font("Lato", Font.PLAIN, 12);

        // --- TOP INFO ROW ---
        topInfoRow = new JPanel(new GridBagLayout());
        topInfoRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        Color bg = UIManager.getColor("background");

        // LEFTMOST (logo)
        JPanel leftmostPanel = new JPanel(new GridBagLayout());
        leftmostPanel.setOpaque(true);
        leftmostPanel.setBackground(bg);
        leftmostPanel.setPreferredSize(new Dimension(92, HEADER_ROW_HEIGHT));
        leftmostPanel.setMinimumSize(new Dimension(80, HEADER_ROW_HEIGHT));
        logoLabel = new JLabel(iconCreator.getIcon("Icons/lightmode/laundromatLogo.svg", LOGO_ICON_SIZE, LOGO_ICON_SIZE));
        GridBagConstraints lg = new GridBagConstraints();
        lg.anchor = GridBagConstraints.CENTER;
        leftmostPanel.add(logoLabel, lg);
        leftmostPanel.setBorder(new EmptyBorder(10, 14, 10, 18));
        gbc.gridx = 0;
        gbc.weightx = 0;
        topInfoRow.add(leftmostPanel, gbc);

        // INNER LEFT (name + address)
        JPanel innerLeftPanel = new JPanel(new GridBagLayout());
        innerLeftPanel.setOpaque(true);
        innerLeftPanel.setBackground(bg);
        innerLeftPanel.setBorder(new EmptyBorder(8, 8, 8, 2));
        innerLeftPanel.setPreferredSize(new Dimension(420, HEADER_ROW_HEIGHT));
        innerLeftPanel.setMinimumSize(new Dimension(160, HEADER_ROW_HEIGHT));

        GridBagConstraints il = new GridBagConstraints();
        il.gridx = 0;
        il.weightx = 1.0;
        il.fill = GridBagConstraints.HORIZONTAL;

        il.gridy = 0; il.weighty = 1.0;
        innerLeftPanel.add(Box.createVerticalGlue(), il);

        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        upperPanel.setOpaque(false);
        nameLabel = new JLabel("Laundromat name");
        nameLabel.setFont(fredokaTitle);
        nameLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
        upperPanel.add(nameLabel);

        il.gridy = 1; il.weighty = 0.0;
        innerLeftPanel.add(upperPanel, il);

        JPanel lowerPanel = new JPanel(new BorderLayout(6, 0));
        lowerPanel.setOpaque(false);
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        // ADDED: keep label as a field for theme swapping
        addrIconLabel = new JLabel(iconCreator.getIcon("Icons/address.svg", 12, 12));
        iconPanel.add(addrIconLabel);

        addressEllipsisLabel = new EllipsisLabel("");
        addressEllipsisLabel.setFont(latoBase);
        addressEllipsisLabel.setForeground(UIManager.getColor("Label.foreground"));

        lowerPanel.add(iconPanel, BorderLayout.WEST);
        lowerPanel.add(addressEllipsisLabel, BorderLayout.CENTER);

        il.gridy = 2; il.weighty = 0.0;
        innerLeftPanel.add(lowerPanel, il);

        il.gridy = 3; il.weighty = 1.0;
        innerLeftPanel.add(Box.createVerticalGlue(), il);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        topInfoRow.add(innerLeftPanel, gbc);

        // DIVIDER
        JPanel dividerWrapper = new JPanel(new GridBagLayout());
        dividerWrapper.setOpaque(false);
        verticalSep = new JSeparator(SwingConstants.VERTICAL);
        Color accent = getAccentBorderColor();
        verticalSep.setForeground(accent);
        verticalSep.setBackground(accent);
        verticalSep.setOpaque(true);
        verticalSep.setPreferredSize(new Dimension(2, HEADER_ROW_HEIGHT - 24));
        verticalSep.setMinimumSize(new Dimension(2, 0));
        verticalSep.setMaximumSize(new Dimension(2, Integer.MAX_VALUE));
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
        innerRightPanel.setPreferredSize(new Dimension(140, HEADER_ROW_HEIGHT));
        innerRightPanel.setMinimumSize(new Dimension(120, HEADER_ROW_HEIGHT));

        JPanel distanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        distanceRow.setOpaque(false);
        // ADDED: use field for theme swapping
        distIconLabel = new JLabel(iconCreator.getIcon("Icons/distancefromUser.svg", 12, 12));
        distanceLabel = new JLabel("0.0 km");
        distanceLabel.setFont(latoBase);
        distanceRow.add(distIconLabel);
        distanceRow.add(distanceLabel);

        JPanel deliveryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        deliveryRow.setOpaque(false);
        // ADDED: use field for theme swapping
        delIconLabel = new JLabel(iconCreator.getIcon("Icons/deliveryperiod.svg", 12, 12));
        deliveryLabel = new JLabel("1–2 days");
        deliveryLabel.setFont(latoBase);
        deliveryRow.add(delIconLabel);
        deliveryRow.add(deliveryLabel);

        innerRightPanel.add(Box.createVerticalGlue());
        innerRightPanel.add(distanceRow);
        innerRightPanel.add(Box.createVerticalStrut(8));
        innerRightPanel.add(deliveryRow);
        innerRightPanel.add(Box.createVerticalGlue());

        gbc.gridx = 3;
        gbc.weightx = 0;
        topInfoRow.add(innerRightPanel, gbc);

        // RIGHTMOST (rating)
        JPanel rightmostPanel = new JPanel(new GridBagLayout());
        rightmostPanel.setOpaque(true);
        rightmostPanel.setBackground(bg);
        rightmostPanel.setPreferredSize(new Dimension(140, HEADER_ROW_HEIGHT));
        rightmostPanel.setMinimumSize(new Dimension(100, HEADER_ROW_HEIGHT));
        GridBagConstraints rg = new GridBagConstraints();
        rg.gridx = 0;
        rg.gridy = 0;
        rg.anchor = GridBagConstraints.EAST;
        rg.insets = new Insets(0, 0, 0, 8);
        JPanel starRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        starRow.setOpaque(false);
        // ADDED: use field for theme swapping
        starIconLabel = new JLabel(iconCreator.getIcon("Icons/lightmode/star.svg", 18, 18));
        ratingLabel = new JLabel("0.0");
        ratingLabel.setFont(fredokaTitle);
        starRow.add(starIconLabel);
        starRow.add(ratingLabel);
        rightmostPanel.add(starRow, rg);

        gbc.gridx = 4;
        gbc.weightx = 0;
        topInfoRow.add(rightmostPanel, gbc);

        headerPanel.add(topInfoRow, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Build center content once: Left | fixed 40px gap horizontally | Right
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
        descriptionArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        descriptionArea.setForeground(UIManager.getColor("Label.foreground"));
        applyLatoBodyFont();

        // Reviews list that tracks viewport width (prevents overflow after resize)
        reviewsPanel = new ScrollablePanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setOpaque(false);
        // Leave some right padding so cards don't touch the scrollbar
        reviewsPanel.setBorder(new EmptyBorder(0, 0, 0, 8));

        // Services: full-width, fixed 3 columns, no outer padding
        servicesPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        servicesPanel.setOpaque(false);
        servicesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        Color bg = UIManager.getColor("background");

        // Constant 60/40 horizontal split and fixed 40px gap horizontally (columns)
        centerContentPanel = new JPanel(new TwoColumnFixedGapLayout(0.60, 40));
        centerContentPanel.setOpaque(false);

        // OUTER LEFT: titles + cards using GridBag
        JPanel outerLeftPanel = new JPanel(new GridBagLayout()) {
            @Override public Dimension getMinimumSize() { return new Dimension(0, super.getMinimumSize().height); }
        };
        outerLeftPanel.setOpaque(false);
        // 20px top padding here gives a net 20px gap from header
        outerLeftPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        GridBagConstraints ol = new GridBagConstraints();
        ol.gridx = 0;
        ol.fill = GridBagConstraints.BOTH;
        ol.weightx = 1.0;

        // Section title: Highlights (outside card border)
        highlightsTitleLabel = new JLabel("Highlights");
        highlightsTitleLabel.setBorder(new EmptyBorder(0, 4, 6, 0));
        ol.gridy = 0;
        ol.weighty = 0;
        ol.insets = new Insets(0, 0, 0, 0);
        outerLeftPanel.add(highlightsTitleLabel, ol);

        // Highlights card (descriptionPanel) with 40px internal padding
        descriptionPanel = new roundedPanel(18);
        descriptionPanel.setOpaque(true);
        descriptionPanel.setBackground(bg);
        descriptionPanel.setLayout(new BorderLayout(0, 0));
        setPanelBorder(descriptionPanel, 18, 40); // 40px internal padding as requested
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        ol.gridy = 1;
        ol.weighty = 0.70;
        descriptionPanel.add(descScroll, BorderLayout.CENTER);
        outerLeftPanel.add(descriptionPanel, ol);

        // Section title: Services Offered
        servicesTitleLabel = new JLabel("Services Offered");
        servicesTitleLabel.setBorder(new EmptyBorder(0, 4, 6, 0));
        ol.gridy = 2;
        ol.weighty = 0;
        ol.insets = new Insets(20, 0, 0, 0);
        outerLeftPanel.add(servicesTitleLabel, ol);
        ol.insets = new Insets(0, 0, 0, 0);

        // Services container
        ol.gridy = 3;
        ol.weighty = 0.30;
        outerLeftPanel.add(servicesPanel, ol);

        // OUTER RIGHT: reviews title + card + button
        JPanel outerRightPanel = new JPanel(new VerticalTopFillBottomFixedGapLayout(20)) {
            @Override public Dimension getMinimumSize() { return new Dimension(0, super.getMinimumSize().height); }
        };
        outerRightPanel.setOpaque(false);
        outerRightPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel reviewsTopGroup = new JPanel(new BorderLayout());
        reviewsTopGroup.setOpaque(false);

        reviewsTitleLabel = new JLabel("Reviews");
        reviewsTitleLabel.setBorder(new EmptyBorder(0, 4, 6, 0));
        reviewsTopGroup.add(reviewsTitleLabel, BorderLayout.NORTH);

        reviewsWrapperPanel = new roundedPanel(18);
        reviewsWrapperPanel.setOpaque(true);
        reviewsWrapperPanel.setBackground(bg);
        reviewsWrapperPanel.setLayout(new BorderLayout(0, 0));
        // Keep reviews card at default 16px padding
        setPanelBorder(reviewsWrapperPanel, 18, 16);

        // Reviews scroll: vertical only; style scrollbar like laundromat list panel
        reviewsScrollPane = new JScrollPane(reviewsPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        reviewsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        reviewsScrollPane.setOpaque(false);
        reviewsScrollPane.getViewport().setOpaque(false);
        reviewsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        Color scrollbarColor = isDarkTheme()
                ? (UIManager.getColor("ScrollBar.thumbDarkShadow") != null
                    ? UIManager.getColor("ScrollBar.thumbDarkShadow")
                    : new Color(0x4A90E2))
                : getLightModeBlue();
        customizeScrollbar(reviewsScrollPane.getVerticalScrollBar(), scrollbarColor);

        reviewsWrapperPanel.add(reviewsScrollPane, BorderLayout.CENTER);
        reviewsTopGroup.add(reviewsWrapperPanel, BorderLayout.CENTER);

        // Request pickup button
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

        // Lime green hover same as laundromat cards (light & dark)
        final Color limeHover = UIManager.getColor("Sidebar.hoverBackground") != null
                ? UIManager.getColor("Sidebar.hoverBackground")
                : new Color(0xDAEC73);
        pickupBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                pickupBtn.setBackground(limeHover);
                pickupBtn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                Color bgBtn = UIManager.getColor("Button.background");
                pickupBtn.setBackground(bgBtn);
                pickupBtn.repaint();
            }
        });

        // Set font to Fredoka Bold; auto-scale to ~90% width
        Font base = UIManager.getFont("Button.font");
        float baseSize = base != null ? base.getSize2D() : 13f;
        Font fredokaBold = getFredokaBold(baseSize, base != null ? base.deriveFont(Font.BOLD) : new Font("Dialog", Font.BOLD, Math.round(baseSize)));
        pickupBtn.setCustomFont(fredokaBold);
        pickupBtn.enableAutoScaleToWidth(0.90f, 12f, 26f);

        // Fixed height
        Dimension ph = pickupBtn.getPreferredSize();
        pickupBtnFixedHeight = Math.max(ph.height + 20, ph.height);
        pickupBtn.setPreferredSize(new Dimension(ph.width, pickupBtnFixedHeight));
        pickupBtn.setMinimumSize(new Dimension(0, pickupBtnFixedHeight));
        pickupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, pickupBtnFixedHeight));

        outerRightPanel.add(reviewsTopGroup, VerticalTopFillBottomFixedGapLayout.TOP);
        outerRightPanel.add(pickupBtn, VerticalTopFillBottomFixedGapLayout.BOTTOM);

        centerContentPanel.add(outerLeftPanel, TwoColumnFixedGapLayout.LEFT);
        centerContentPanel.add(outerRightPanel, TwoColumnFixedGapLayout.RIGHT);

        add(centerContentPanel, BorderLayout.CENTER);

        // After layout, rescale button and sync fonts so sizes match your spec
        SwingUtilities.invokeLater(() -> {
            pickupBtn.rescaleNow();
            applySectionTitleFonts();
            applyHeaderFontSizes();
            applyThemeStyling();
            applyTextColors();
            applyIconTheme(); // ensure icons match current theme
            // Keep initial position at top on first build only
            scrollReviewsToTop();
        });
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

    private void applySectionTitleFonts() {
        float sz = 14f;
        if (pickupBtn != null && pickupBtn.getLabelFont() != null) {
            sz = pickupBtn.getLabelFont().getSize2D();
        } else {
            Font title = UIManager.getFont("Title.font");
            if (title != null) sz = title.getSize2D();
        }
        Font fallback = new Font("Dialog", Font.BOLD, Math.round(sz));
        Font fredokaBold = getFredokaBold(sz, fallback);

        if (highlightsTitleLabel != null) highlightsTitleLabel.setFont(fredokaBold);
        if (servicesTitleLabel != null) servicesTitleLabel.setFont(fredokaBold);
        if (reviewsTitleLabel != null) reviewsTitleLabel.setFont(fredokaBold);
    }

    // Synchronize header font sizes to requested references
    // - name and rating => same size as "Request Pickup" button text (Fredoka Bold)
    // - address/distance/delivery => same size as description body text (Lato/plain)
    private void applyHeaderFontSizes() {
        if (pickupBtn != null && pickupBtn.getLabelFont() != null) {
            Font btnFont = pickupBtn.getLabelFont();
            Font fredokaBold = getFredokaBold(btnFont.getSize2D(),
                    new Font("Dialog", Font.BOLD, Math.round(btnFont.getSize2D())));
            if (nameLabel != null) nameLabel.setFont(fredokaBold);
            if (ratingLabel != null) ratingLabel.setFont(fredokaBold);
        }

        Font body = (descriptionArea != null && descriptionArea.getFont() != null)
                ? descriptionArea.getFont()
                : (UIManager.getFont("defaultFont") != null ? UIManager.getFont("defaultFont")
                : new Font("Dialog", Font.PLAIN, 12));

        if (addressEllipsisLabel != null) {
            addressEllipsisLabel.setFont(body);
            addressEllipsisLabel.refreshClip();
        }
        if (distanceLabel != null) distanceLabel.setFont(body);
        if (deliveryLabel != null) deliveryLabel.setFont(body);
    }

    // Scroll viewport to top explicitly for initial show and after list population
    private void scrollReviewsToTop() {
        if (reviewsScrollPane != null) {
            JViewport vp = reviewsScrollPane.getViewport();
            if (vp != null) vp.setViewPosition(new Point(0, 0));
            JScrollBar bar = reviewsScrollPane.getVerticalScrollBar();
            if (bar != null) bar.setValue(bar.getMinimum());
        }
    }

    // Capture/restore helpers to preserve scroll across theme toggles
    private int captureReviewsScroll() {
        if (reviewsScrollPane == null) return -1;
        JScrollBar bar = reviewsScrollPane.getVerticalScrollBar();
        return (bar != null) ? bar.getValue() : -1;
    }

    private void restoreReviewsScroll(int value) {
        if (reviewsScrollPane == null) return;
        JScrollBar bar = reviewsScrollPane.getVerticalScrollBar();
        if (bar != null && value >= bar.getMinimum() && value <= bar.getMaximum()) {
            bar.setValue(value);
        }
    }

    private void withReviewsScrollPreserved(Runnable r) {
        int v = captureReviewsScroll();
        try {
            r.run();
        } finally {
            if (v >= 0) restoreReviewsScroll(v);
        }
    }

    // Ensure all text colors follow the current theme
    private void applyTextColors() {
        Color fg = UIManager.getColor("Label.foreground");
        if (fg == null) fg = Color.BLACK;

        if (nameLabel != null) nameLabel.setForeground(fg);
        if (ratingLabel != null) ratingLabel.setForeground(fg);

        if (addressEllipsisLabel != null) {
            addressEllipsisLabel.setForeground(fg);
            addressEllipsisLabel.refreshClip();
        }
        if (distanceLabel != null) distanceLabel.setForeground(fg);
        if (deliveryLabel != null) deliveryLabel.setForeground(fg);

        if (highlightsTitleLabel != null) highlightsTitleLabel.setForeground(fg);
        if (servicesTitleLabel != null) servicesTitleLabel.setForeground(fg);
        if (reviewsTitleLabel != null) reviewsTitleLabel.setForeground(fg);

        if (descriptionArea != null) descriptionArea.setForeground(fg);
    }

    /** Populate UI when a laundromat is clicked. */
    public void setLaundromat(LaundromatData data) {
        if (placeholderWrapper.getParent() != null) {
            remove(placeholderWrapper);
            add(headerPanel, BorderLayout.NORTH);
            add(centerContentPanel, BorderLayout.CENTER);
        }

        // update fields (icons handled by applyIconTheme)
        nameLabel.setText(data.name);
        if (addressEllipsisLabel != null) addressEllipsisLabel.setFullText(data.address);
        distanceLabel.setText(data.distance);
        deliveryLabel.setText(data.deliveryPeriod);

        double rating = getRatingFor(data);
        ratingLabel.setText(String.format("%.1f", rating));

        // Update Highlights
        String[] bullets = null;

// 1) If DTO already has highlights populated, use it
if (data != null && data.highlights != null && !data.highlights.trim().isEmpty()) {
    bullets = parseHighlightsString(data.highlights);
}

// 2) If no highlights in DTO, try best-effort DB lookup by name+address, then name-only
if ((bullets == null || bullets.length == 0) && data != null) {
    try {
        java.sql.Connection conn = org.example.database.DBConnect.getConnection();
        if (conn != null && !conn.isClosed()) {
            String q = "SELECT laundromatID, pricePerLoad, highlights FROM laundromat WHERE laundromatName = ? AND laundromatAddress = ? LIMIT 1";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(q)) {
                ps.setString(1, data.name != null ? data.name.trim() : "");
                ps.setString(2, data.address != null ? data.address.trim() : "");
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // populate DTO so other code can use it
                        double price = rs.getDouble("pricePerLoad");
                        String dbHighlights = rs.getString("highlights");
                        data.pricePerLoad = price;
                        data.highlights = dbHighlights;
                        if (dbHighlights != null && !dbHighlights.trim().isEmpty()) {
                            bullets = parseHighlightsString(dbHighlights);
                        }
                    }
                }
            }

            // fallback: try name-only match if still no bullets
            if ((bullets == null || bullets.length == 0) && data.name != null) {
                String q2 = "SELECT laundromatID, pricePerLoad, highlights FROM laundromat WHERE laundromatName = ? LIMIT 1";
                try (java.sql.PreparedStatement ps2 = conn.prepareStatement(q2)) {
                    ps2.setString(1, data.name.trim());
                    try (java.sql.ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) {
                            double price2 = rs2.getDouble("pricePerLoad");
                            String dbHighlights2 = rs2.getString("highlights");
                            data.pricePerLoad = price2;
                            data.highlights = dbHighlights2;
                            if (dbHighlights2 != null && !dbHighlights2.trim().isEmpty()) {
                                bullets = parseHighlightsString(dbHighlights2);
                            }
                        }
                    }
                }
            }
        }
    } catch (Exception ex) {
        // best-effort: if DB fails, continue and fall back to curated list
        ex.printStackTrace();
    }
}

// 3) Final fallback to curated, hard-coded highlights (preserves previous behavior)
if (bullets == null || bullets.length == 0) {
    bullets = getHighlightsFor(data);
}

// 4) Append price bullet (last)
java.util.List<String> bulletList = new java.util.ArrayList<>(java.util.Arrays.asList(bullets != null ? bullets : new String[0]));
if (data != null && data.pricePerLoad > 0.0) {
    bulletList.add(String.format("Price per load: \u20B1%.2f", data.pricePerLoad));
} else {
    bulletList.add("Price per load: N/A");
}

// 5) Set highlights into the existing renderer
setHighlights(bulletList.toArray(new String[0]));

        // Populate reviews (exactly 5 review cards per laundromat, with 20px gaps)
        reviewsPanel.removeAll();
        List<ReviewCard> cards = getReviewsFor(data);
        for (int i = 0; i < cards.size(); i++) {
            ReviewCard card = cards.get(i);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            reviewsPanel.add(card);
            if (i < cards.size() - 1) reviewsPanel.add(Box.createVerticalStrut(20));
        }
        // Ensure scrollbar is at top on first population
        SwingUtilities.invokeLater(this::scrollReviewsToTop);

        // Populate services
        servicesPanel.removeAll();
        servicesPanel.add(createServiceItem("Wash & Fold", "Icons/Services/washandFold.svg"));
        servicesPanel.add(createServiceItem("Dry Clean", "Icons/Services/dryClean.svg"));
        servicesPanel.add(createServiceItem("Ironing", "Icons/Services/iron.svg"));
        updateServiceCardBorders(); // reflect theme

        SwingUtilities.invokeLater(() -> {
            if (pickupBtn != null) pickupBtn.rescaleNow();
            applySectionTitleFonts();
            applyHeaderFontSizes();
            // Preserve scroll while applying theme styling (in case LAF changes happened)
            withReviewsScrollPreserved(() -> {
                applyThemeStyling();
                applyTextColors();
                applyIconTheme(); // ensure icons match current theme
            });
        });

        revalidate();
        repaint();
        try {
    Connection conn = DBConnect.getConnection();
    if (conn != null && !conn.isClosed()) {
        String q = "SELECT laundromatID, pricePerLoad FROM laundromat WHERE laundromatName = ? AND laundromatAddress = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, data.name != null ? data.name.trim() : "");
            ps.setString(2, data.address != null ? data.address.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("laundromatID");
                    double price = rs.getDouble("pricePerLoad");
                    data.pricePerLoad = price; // populate DTO so UI can read it
                    AppState.setSelectedLaundromat(id, data.name);
                } else {
                    // fallback: try match by name only (first match)
                    String q2 = "SELECT laundromatID, pricePerLoad FROM laundromat WHERE laundromatName = ? LIMIT 1";
                    try (PreparedStatement ps2 = conn.prepareStatement(q2)) {
                        ps2.setString(1, data.name != null ? data.name.trim() : "");
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                int id2 = rs2.getInt("laundromatID");
                                double price2 = rs2.getDouble("pricePerLoad");
                                data.pricePerLoad = price2;
                                AppState.setSelectedLaundromat(id2, data.name);
                            } else {
                                // no match: clear selection
                                AppState.clearSelectedLaundromat();
                            }
                        }
                    }
                }
            }
        }
    } else {
        System.err.println("[LaundromatDetailsPanel] DB connection null — selection not saved.");
    }
} catch (Exception ex) {
    ex.printStackTrace();
    AppState.clearSelectedLaundromat();
}
    }

    private double getRatingFor(LaundromatData data) {
        String name = data != null && data.name != null ? data.name.trim() : "";
        switch (name) {
            case "WashEat Laundry": return 4.7;
            case "La Vahh Laundromat": return 4.5;
            case "Allklean Laundromat": return 4.3;
            case "D'Laundry Station": return 4.2;
            case "RouTine Laundromat Roxas": return 4.4;
            default: return 4.1;
        }
    }

    // Create a single service item with its own rounded border, no inner padding; fills its grid cell
    private JComponent createServiceItem(String title, String iconPath) {
        Color bg = UIManager.getColor("background");

        // Actual service content (transparent ServiceCard)
        JComponent content = new ServiceCard(title, iconPath);

        // Wrap the content in its own rounded card, NO inner padding; border uses theme accent
        roundedPanel card = new roundedPanel(18);
        card.setOpaque(true);
        card.setBackground(bg);
        card.setLayout(new BorderLayout());
        card.setBorder(new roundedBorder(18, getAccentBorderColor(), 2));
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    // Re-apply service card borders when theme changes
    private void updateServiceCardBorders() {
        Color accent = getAccentBorderColor();
        if (servicesPanel != null) {
            for (Component c : servicesPanel.getComponents()) {
                if (c instanceof roundedPanel) {
                    ((roundedPanel) c).setBorder(new roundedBorder(18, accent, 2));
                }
            }
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.background"));
        if (headerPanel != null) headerPanel.setBackground(UIManager.getColor("background"));

        Color bg = UIManager.getColor("background");
        if (descriptionPanel != null) descriptionPanel.setBackground(bg);
        if (reviewsWrapperPanel != null) reviewsWrapperPanel.setBackground(bg);

        SwingUtilities.invokeLater(() -> {
            if (pickupBtn != null) {
                if (pickupBtnFixedHeight > 0) {
                    Dimension ph = pickupBtn.getPreferredSize();
                    pickupBtn.setPreferredSize(new Dimension(ph.width, pickupBtnFixedHeight));
                    pickupBtn.setMinimumSize(new Dimension(0, pickupBtnFixedHeight));
                    pickupBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, pickupBtnFixedHeight));
                }
                pickupBtn.rescaleNow();
            }
            applySectionTitleFonts();
            applyHeaderFontSizes();
            // Preserve current scroll position when theme/UI updates occur
            withReviewsScrollPreserved(() -> {
                applyThemeStyling();
                applyTextColors();
                applyIconTheme(); // ensure icons match current theme
            });
        });

        applyLatoBodyFont();

        revalidate();
        repaint();
    }

    // --- Theme helpers: borders, divider, scrollbar ---

    private void applyThemeStyling() {
        withReviewsScrollPreserved(() -> {
            Color accent = getAccentBorderColor();

            // Header border
            if (headerPanel != null) {
                headerPanel.setBorder(BorderFactory.createCompoundBorder(
                        new roundedBorder(18, accent, 2),
                        new EmptyBorder(20, 24, 20, 24)
                ));
            }
            // Divider line between left and right header blocks
            if (verticalSep != null) {
                verticalSep.setForeground(accent);
                verticalSep.setBackground(accent);
                verticalSep.setOpaque(true);
                verticalSep.repaint();
            }
            // Highlights (40px) and Reviews (16px) cards
            if (descriptionPanel != null) setPanelBorder(descriptionPanel, 18, 40);
            if (reviewsWrapperPanel != null) setPanelBorder(reviewsWrapperPanel, 18, 16);

            // Style reviews scrollbar same as list panel
            if (reviewsScrollPane != null) {
                Color scrollbarColor = isDarkTheme()
                        ? (UIManager.getColor("ScrollBar.thumbDarkShadow") != null
                            ? UIManager.getColor("ScrollBar.thumbDarkShadow")
                            : new Color(0x4A90E2))
                        : getLightModeBlue();
                customizeScrollbar(reviewsScrollPane.getVerticalScrollBar(), scrollbarColor);
            }

            // Service card borders must reflect theme (e.g., white in dark mode)
            updateServiceCardBorders();

            // Force repaint
            revalidate();
            repaint();
        });
    }

    // Overload: set rounded border with custom inner padding per panel
    private void setPanelBorder(JComponent panel, int radius, int padding) {
        panel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(radius, getAccentBorderColor(), 2),
                new EmptyBorder(padding, padding, padding, padding)
        ));
    }

    private Color getAccentBorderColor() {
        return isDarkTheme() ? Color.WHITE : getLightModeBlue();
    }

    private Color getLightModeBlue() {
        Color c = UIManager.getColor("Component.accentColor");
        if (c == null) c = UIManager.getColor("Actions.Blue");
        if (c == null) c = new Color(0x2196F3);
        return c;
    }

    // ADDED: swap icons for dark/light themes (includes address icon in header)
    private void applyIconTheme() {
        boolean dark = isDarkTheme();

        // Header logo
        if (logoLabel != null) {
            Icon logo = iconCreator.getIcon(
                    dark ? "Icons/darkmode/laundromatLogoDarkMode.svg" : "Icons/lightmode/laundromatLogo.svg",
                    LOGO_ICON_SIZE, LOGO_ICON_SIZE
            );
            logoLabel.setIcon(logo);
        }

        // Address icon (12x12)
        if (addrIconLabel != null) {
            addrIconLabel.setIcon(iconCreator.getIcon(
                    dark ? "Icons/darkmode/addressDarkMode.svg" : "Icons/lightmode/address.svg",
                    12, 12));
        }

        // Distance and delivery icons (12x12)
        if (distIconLabel != null) {
            distIconLabel.setIcon(iconCreator.getIcon(
                    dark ? "Icons/darkmode/distancefromUserDarkMode.svg" : "Icons/lightmode/distancefromUser.svg",
                    12, 12));
        }
        if (delIconLabel != null) {
            delIconLabel.setIcon(iconCreator.getIcon(
                    dark ? "Icons/darkmode/deliveryperiodDarkMode.svg" : "Icons/lightmode/deliveryperiod.svg",
                    12, 12));
        }

        // Star icon (18x18)
        if (starIconLabel != null) {
            starIconLabel.setIcon(iconCreator.getIcon(
                    dark ? "Icons/darkmode/starDarkMode.svg" : "Icons/lightmode/star.svg",
                    18, 18));
        }
    }

    // More robust dark theme detection across LAFs
    private boolean isDarkTheme() {
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) bg = UIManager.getColor("background");
        if (bg == null) bg = getBackground();
        if (bg == null) return false;
        double luminance = (0.299 * bg.getRed()) + (0.587 * bg.getGreen()) + (0.114 * bg.getBlue());
        return luminance < 128;
    }

    // Scrollbar customization — same approach as LaundromatListPanel
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

    // --- Helpers: Highlights (bulleted badges) ---

    private void applyLatoBodyFont() {
        if (descriptionArea == null) return;
        Font base = UIManager.getFont("defaultFont");
        float size = base != null ? base.getSize2D() : 12f;
        Font lato = new Font("Lato", Font.PLAIN, Math.round(size));
        if ("Dialog".equalsIgnoreCase(lato.getFamily()) && base != null) {
            lato = base.deriveFont(Font.PLAIN, size);
        }
        descriptionArea.setFont(lato);
    }

    private void setHighlights(String[] bullets) {
        if (bullets == null) {
            descriptionArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bullets.length; i++) {
            String line = bullets[i];
            if (line != null && !line.trim().isEmpty()) {
                sb.append("• ").append(line.trim());
                if (i < bullets.length - 1) sb.append("\n");
            }
        }
        descriptionArea.setText(sb.toString());
        descriptionArea.setCaretPosition(0);
    }
    
    private String[] parseHighlightsString(String raw) {
    if (raw == null) return new String[0];
    String s = raw.trim();
    String[] parts;
    if (s.contains(";")) {
        parts = s.split("\\s*;\\s*");
    } else if (s.contains("\n")) {
        parts = s.split("\\r?\\n");
    } else if (s.contains(",")) {
        parts = s.split("\\s*,\\s*");
    } else {
        parts = new String[]{ s };
    }
    List<String> out = new ArrayList<>();
    for (String p : parts) {
        if (p != null) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
    }
    return out.toArray(new String[0]);
}

    private String[] getHighlightsFor(LaundromatData data) {
    // 1) prefer DTO-provided highlights
    if (data != null && data.highlights != null && !data.highlights.trim().isEmpty()) {
        return parseHighlightsString(data.highlights);
    }

    // 2) try DB lookup (best-effort)
    if (data != null && (data.name != null && !data.name.trim().isEmpty())) {
        try {
            Connection conn = org.example.database.DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                // Try name + address (most specific)
                if (data.address != null && !data.address.trim().isEmpty()) {
                    String sql = "SELECT highlights, pricePerLoad FROM laundromat WHERE laundromatName = ? AND laundromatAddress = ? LIMIT 1";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, data.name.trim());
                        ps.setString(2, data.address.trim());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                String dbHighlights = rs.getString("highlights");
                                double dbPrice = rs.getDouble("pricePerLoad");
                                if (dbHighlights != null && !dbHighlights.trim().isEmpty()) {
                                    data.highlights = dbHighlights;         // populate DTO
                                    data.pricePerLoad = dbPrice;           // populate DTO for later use
                                    return parseHighlightsString(dbHighlights);
                                } else {
                                    // still store price if present
                                    data.pricePerLoad = dbPrice;
                                }
                            }
                        }
                    }
                }

                // Fallback: try by name only
                String sqlNameOnly = "SELECT highlights, pricePerLoad FROM laundromat WHERE laundromatName = ? LIMIT 1";
                try (PreparedStatement ps2 = conn.prepareStatement(sqlNameOnly)) {
                    ps2.setString(1, data.name.trim());
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) {
                            String dbHighlights2 = rs2.getString("highlights");
                            double dbPrice2 = rs2.getDouble("pricePerLoad");
                            if (dbHighlights2 != null && !dbHighlights2.trim().isEmpty()) {
                                data.highlights = dbHighlights2;
                                data.pricePerLoad = dbPrice2;
                                return parseHighlightsString(dbHighlights2);
                            } else {
                                // still store price
                                data.pricePerLoad = dbPrice2;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // best-effort: log and continue to fallback
            ex.printStackTrace();
        }
    }

    // 3) final fallback -- keep a small generic list instead of the per-name hardcoding
    return new String[] {
            "Quality care for your garments",
            "Sanitized machines",
            "Consistent folding standard",
            "Rewash guarantee"
    };
}

    // Five review cards per laundromat with curated names and comments
    private List<ReviewCard> getReviewsFor(LaundromatData data) {
        String name = data != null && data.name != null ? data.name.trim() : "";
        switch (name) {
            case "WashEat Laundry":
                return Arrays.asList(
                    new ReviewCard("Maria S.", "Dropped off at midnight and still got a next‑day turnaround. Clothes were neatly folded and smelled fresh."),
                    new ReviewCard("Kevin L.", "Was worried about a coffee stain, but it’s gone. Pricing feels fair for 24/7 convenience."),
                    new ReviewCard("Priya N.", "Hypoallergenic option really helped. No irritation at all, and towels are super soft."),
                    new ReviewCard("Daniel C.", "Machines looked sanitized and the staff was responsive on pickup time."),
                    new ReviewCard("Hannah R.", "Comforters came back fluffy and sealed. Very happy with the packaging.")
                );
            case "La Vahh Laundromat":
                return Arrays.asList(
                    new ReviewCard("Alyssa P.", "They handled delicates perfectly. No shrinking, no color bleed. Will return."),
                    new ReviewCard("Mark T.", "Smells great, folded consistently, and finished earlier than quoted."),
                    new ReviewCard("Jen K.", "Requested hand‑finished for a blouse. Looked crisp without harsh creases."),
                    new ReviewCard("Ruben V.", "Staff messaged when it was ready. Smooth experience from drop‑off to pickup."),
                    new ReviewCard("Sam D.", "Good value for the care they put into each item.")
                );
            case "Allklean Laundromat":
                return Arrays.asList(
                    new ReviewCard("Nina G.", "Deep cleaned our duvet and it looks new again. They handled bulk items well."),
                    new ReviewCard("Owen R.", "Pre‑treated a sauce stain on jeans. Totally gone."),
                    new ReviewCard("Lara M.", "Machines look sanitized and the folding was tidy. Reliable spot."),
                    new ReviewCard("Jae H.", "Gave us a small discount for multiple bags. Appreciate the transparency."),
                    new ReviewCard("Victor A.", "Sturdy packaging with clear labels. Easy to put everything away.")
                );
            case "D'Laundry Station":
                return Arrays.asList(
                    new ReviewCard("Celine W.", "Budget friendly without cutting corners. Fold lines are consistent."),
                    new ReviewCard("Arman F.", "Handled our big week’s load just fine. Nothing missing, nothing mixed up."),
                    new ReviewCard("Patrice Q.", "Requested a rewash on a stubborn stain and they honored it, no fuss."),
                    new ReviewCard("Ivy Z.", "Quick counter service and helpful with sorting instructions."),
                    new ReviewCard("Diego J.", "Good for routine washes. Turnaround matched the estimate.")
                );
            case "RouTine Laundromat Roxas":
                return Arrays.asList(
                    new ReviewCard("Mika E.", "Linens came back host‑ready, folded in sets with size labels. Perfect for our Airbnb."),
                    new ReviewCard("Harvey B.", "Bundled towels per bathroom as requested. Huge time‑saver on turnover."),
                    new ReviewCard("Kayla S.", "Packaging kept sheets dust‑free while stored. Looks professional."),
                    new ReviewCard("Noah P.", "Consistent folding means quick shelf stocking. Appreciate the details."),
                    new ReviewCard("Trixie L.", "Reliable pickup and drop‑off windows. Makes scheduling easier.")
                );
            default:
                return Arrays.asList(
                    new ReviewCard("Janel A.", "Clothes came back clean and neatly folded. Easy drop‑off."),
                    new ReviewCard("Roman I.", "Friendly staff and clear pricing. No surprises."),
                    new ReviewCard("Belle C.", "They removed a makeup stain I thought was permanent."),
                    new ReviewCard("Tom K.", "Turnaround was faster than expected. Will use again."),
                    new ReviewCard("Erin Y.", "Packaging kept everything organized. Good experience overall.")
                );
        }
    }

    // --- Custom Layout classes ---

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
            if (LEFT.equals(constraints)) leftComp = comp;
            else if (RIGHT.equals(constraints)) rightComp = comp;
            else { if (leftComp == null) leftComp = comp; else rightComp = comp; }
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
        @Override public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
        @Override public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int x = in.left, y = in.top;
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
        @Override public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
        @Override public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int x = in.left, y = in.top;
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

    // --- Inner: panel that tracks viewport width to prevent horizontal overflow ---
    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return Math.max(visibleRect.height - 16, 16); }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    // --- Single-line label with "…" ellipsis when text exceeds available width ---
    private static class EllipsisLabel extends JLabel {
        private String fullText = "";

        EllipsisLabel(String text) {
            super();
            this.fullText = text != null ? text : "";
            setToolTipText(null);
            addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    updateClippedText();
                }
            });
            SwingUtilities.invokeLater(this::updateClippedText);
        }

        public void setFullText(String text) {
            this.fullText = text != null ? text : "";
            updateClippedText();
        }

        @Override
        public void setText(String text) {
            setFullText(text);
        }

        public void refreshClip() {
            updateClippedText();
        }

        @Override
        public void updateUI() {
            super.updateUI();
            Color fg = UIManager.getColor("Label.foreground");
            if (fg != null) setForeground(fg);
            SwingUtilities.invokeLater(this::updateClippedText);
        }

        private void updateClippedText() {
            Font font = getFont();
            if (font == null) {
                font = UIManager.getFont("Label.font");
                if (font == null) font = new Font("Dialog", Font.PLAIN, 12);
            }

            Insets in = getInsets();
            int avail = Math.max(0, getWidth() - in.left - in.right);

            if (avail <= 0 || fullText == null) {
                super.setText(fullText != null ? fullText : "");
                setToolTipText(null);
                return;
            }

            FontMetrics fm = getFontMetrics(font);
            String ellipsis = "\u2026";

            int textW = fm.stringWidth(fullText);
            if (textW <= avail) {
                super.setText(fullText);
                setToolTipText(null);
                return;
            }

            int lo = 0, hi = fullText.length();
            int best = 0;
            while (lo <= hi) {
                int mid = (lo + hi) / 2;
                String candidate = fullText.substring(0, mid) + ellipsis;
                int w = fm.stringWidth(candidate);
                if (w <= avail) {
                    best = mid;
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            String clipped = best > 0 ? fullText.substring(0, best) + ellipsis : ellipsis;
            super.setText(clipped);
            setToolTipText(fullText);
        }
    }
}