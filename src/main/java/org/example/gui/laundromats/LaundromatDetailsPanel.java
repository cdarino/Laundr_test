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
    private JTextArea addressArea;  // Added separate field for text area

    private final JPanel placeholderWrapper;
    private final JLabel placeholderIconLabel;

    // Callback to request showing the pickup view (provided by parent Laundromats)
    private final Runnable onRequestPickup;

    public LaundromatDetailsPanel(Runnable onRequestPickup) {
        this.onRequestPickup = onRequestPickup;

        setLayout(new BorderLayout(12, 12));
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

        // LEFTMOST PANEL (logo)
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

        // INNER LEFT PANEL (name + address)
        JPanel innerLeftPanel = new JPanel();
        innerLeftPanel.setLayout(new GridLayout(2, 1, 0, 6));
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

        // INNER RIGHT PANEL (distance + delivery)
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

        // RIGHTMOST PANEL (rating)
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

        // Add header to north
        headerPanel.add(topInfoRow, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // CENTER CONTENT
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        centerPanel.setOpaque(false);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("What We Offer:"));
        centerPanel.add(descScroll);

        reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setOpaque(false);
        JScrollPane revScroll = new JScrollPane(reviewsPanel);
        revScroll.setBorder(BorderFactory.createTitledBorder("Reviews:"));
        centerPanel.add(revScroll);
        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM: services + request pickup button
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
        bottomPanel.setOpaque(false);

        servicesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        servicesPanel.setOpaque(false);
        bottomPanel.add(servicesPanel, BorderLayout.CENTER);

        // Request pickup button using callback
        pickupBtn = new buttonCreator("Request Pickup", "Button.font", () -> {
            if (onRequestPickup != null) onRequestPickup.run();
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(pickupBtn);
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(bottomPanel, BorderLayout.SOUTH);

        // PLACEHOLDER STATE
        placeholderWrapper = new JPanel(new GridBagLayout());
        placeholderWrapper.setOpaque(false);
        placeholderIconLabel = new JLabel(iconCreator.getIcon("Icons/darkmode/laundromatLogoDarkMode.svg", 250, 250));
        placeholderWrapper.add(placeholderIconLabel, new GridBagConstraints());

        // Start with placeholder only
        remove(headerPanel);
        remove(centerPanel);
        remove(bottomPanel);
        add(placeholderWrapper, BorderLayout.CENTER);
    }

    /** Populate UI when a laundromat is clicked. */
    public void setLaundromat(LaundromatData data) {
        if (placeholderWrapper.getParent() != null) {
            remove(placeholderWrapper);
            add(headerPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 0));
            centerPanel.setOpaque(false);
            JScrollPane descScroll = new JScrollPane(descriptionArea);
            descScroll.setBorder(BorderFactory.createTitledBorder("What We Offer:"));
            centerPanel.add(descScroll);
            JScrollPane revScroll = new JScrollPane(reviewsPanel);
            revScroll.setBorder(BorderFactory.createTitledBorder("Reviews:"));
            centerPanel.add(revScroll);
            add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
            bottomPanel.setOpaque(false);
            bottomPanel.add(servicesPanel, BorderLayout.CENTER);

            JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonWrapper.setOpaque(false);
            buttonWrapper.add(pickupBtn);

            bottomPanel.add(buttonWrapper, BorderLayout.EAST);
            bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            add(bottomPanel, BorderLayout.SOUTH);
        }

        // update fields
        logoLabel.setIcon(iconCreator.getIcon("Icons/lightmode/laundromatLogo.svg", 48, 48));
        nameLabel.setText(data.name);
        addressArea.setText(data.address);
        distanceLabel.setText(data.distance);
        deliveryLabel.setText(data.deliveryPeriod);
        ratingLabel.setText(String.format("%.1f", data.stars > 0 ? data.stars : 0.0));

        descriptionArea.setText(data.description != null ? data.description : "");

        reviewsPanel.removeAll();
        Arrays.asList(
                new ReviewCard("John Doe", "Great service! Will definitely come back."),
                new ReviewCard("Jane Doe", "Fast delivery and very clean laundry."),
                new ReviewCard("Alex", "Convenient and affordable.")
        ).forEach(reviewsPanel::add);

        servicesPanel.removeAll();
        servicesPanel.add(new ServiceCard("Wash & Fold", "Icons/Services/washandFold.svg"));
        servicesPanel.add(new ServiceCard("Dry Clean", "Icons/Services/dryClean.svg"));
        servicesPanel.add(new ServiceCard("Ironing", "Icons/Services/iron.svg"));

        revalidate();
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.background"));

        // refresh placeholder icon (theme changes)
        if (placeholderIconLabel != null) {
            placeholderIconLabel.setIcon(iconCreator.getIcon("Icons/darkmode/laundromatLogoDarkMode.svg", 250, 250));
        }

        // Update header panel background
        if (headerPanel != null) {
            headerPanel.setBackground(UIManager.getColor("background"));
        }

        // Update pickup button
        if (pickupBtn != null) {
            pickupBtn.updateUI();
        }

        // keep fonts in sync with theme if UIManager supplies them
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

        // update divider color
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
}