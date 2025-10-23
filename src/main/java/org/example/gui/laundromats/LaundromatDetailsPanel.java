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

    public LaundromatDetailsPanel() {
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

        // Fonts (prefer UIManager-provided fonts; fall back to named fonts)
        Font fredokaMedium16 = UIManager.getFont("Title.font") != null
                ? UIManager.getFont("Title.font").deriveFont(Font.BOLD, 16f)
                : new Font("Fredoka Medium", Font.BOLD, 16);
        Font fredoka16 = fredokaMedium16;
        Font lato9 = UIManager.getFont("defaultFont") != null
                ? UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 9f)
                : new Font("Lato", Font.PLAIN, 9);

        // --- TOP INFO ROW: use GridBagLayout with 5 columns (leftmost, inner-left, divider, inner-right, rightmost)
        topInfoRow = new JPanel(new GridBagLayout());
        topInfoRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        Color dividerColor = UIManager.getColor("listBorder");
        Color bg = UIManager.getColor("background");

        // === LEFTMOST PANEL (fixed width) ===
        JPanel leftmostPanel = new JPanel(new GridBagLayout()); // GridBag centers children by default
        leftmostPanel.setOpaque(true);
        leftmostPanel.setBackground(bg);
        leftmostPanel.setPreferredSize(new Dimension(80, 80)); // fixed-ish width to anchor logo
        leftmostPanel.setMinimumSize(new Dimension(70, 60));

        logoLabel = new JLabel(iconCreator.getIcon("Icons/lightmode/laundromatLogo.svg", 48, 48));
        // Add logo centered
        GridBagConstraints lg = new GridBagConstraints();
        lg.anchor = GridBagConstraints.CENTER;
        leftmostPanel.add(logoLabel, lg);
        leftmostPanel.setBorder(new EmptyBorder(0, 0, 0, 12));  // Add right margin to separate from name

        gbc.gridx = 0;
        gbc.weightx = 0; // no expansion
        topInfoRow.add(leftmostPanel, gbc);

        // === INNER LEFT PANEL (name + address) ===
        JPanel innerLeftPanel = new JPanel();
        innerLeftPanel.setLayout(new GridLayout(2, 1, 0, 6)); // 2 rows, 1 column, 6px vertical gap
        innerLeftPanel.setOpaque(true);
        innerLeftPanel.setBackground(bg);
        innerLeftPanel.setBorder(new EmptyBorder(8, 8, 8, 2));  // Reduce right padding from 8 to 2
        // Allowed to expand to take remaining space between anchored columns
        innerLeftPanel.setPreferredSize(new Dimension(420, 80));  // Increased width to accommodate longer addresses
        innerLeftPanel.setMinimumSize(new Dimension(160, 60));

        // Upper panel for name (with left padding to align with address text)
        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        upperPanel.setOpaque(false);
        nameLabel = new JLabel("Laundromat name");
        nameLabel.setFont(fredoka16);
        nameLabel.setBorder(new EmptyBorder(0, 3, 0, 0));  // 18px = 12px (icon width) + 6px (gap)
        upperPanel.add(nameLabel);

        // Lower panel for address
        JPanel lowerPanel = new JPanel(new BorderLayout(6, 0));  // 6px gap between icon and text
        lowerPanel.setOpaque(false);
        
        // Create icon panel to prevent the icon from stretching
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);  // Align icon panel to top
        JLabel addrIcon = new JLabel(iconCreator.getIcon("Icons/address.svg", 12, 12));
        iconPanel.add(addrIcon);
        
        // Configure address as a text area for text wrapping
        addressArea = new JTextArea("Address goes here");
        addressArea.setFont(lato9);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);  // Wrap at word boundaries
        addressArea.setEditable(false);
        addressArea.setOpaque(false);
        addressArea.setBorder(new EmptyBorder(2, 0, 0, 0));  // Add top padding to align with icon
        addressArea.setForeground(UIManager.getColor("Label.foreground"));
        addressArea.setAlignmentY(JTextArea.TOP_ALIGNMENT);  // Align to top to match icon
        
        // Set rows to 2 to accommodate two lines of text
        addressArea.setRows(2);
        // Calculate preferred size based on font metrics
        FontMetrics fm = addressArea.getFontMetrics(lato9);
        int lineHeight = fm.getHeight();
        addressArea.setPreferredSize(new Dimension(0, lineHeight * 2 + 4)); // 2 lines + padding

        // Keep a reference for the label (used in other parts of the code)
        addressLabel = new JLabel();
        addressLabel.setFont(lato9);
        
        // Add components: icon fixed at WEST, address takes remaining space
        lowerPanel.add(iconPanel, BorderLayout.WEST);
        lowerPanel.add(addressArea, BorderLayout.CENTER);

        // Add panels to inner left panel
        innerLeftPanel.add(upperPanel);
        innerLeftPanel.add(lowerPanel);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // this column takes available extra width
        gbc.anchor = GridBagConstraints.WEST; // Force components to stay left-aligned
        topInfoRow.add(innerLeftPanel, gbc);

        // === DIVIDER (fixed) ===
        JPanel dividerWrapper = new JPanel(new GridBagLayout());
        dividerWrapper.setOpaque(false);
        JSeparator verticalSep = new JSeparator(SwingConstants.VERTICAL);
        verticalSep.setForeground(dividerColor);
        verticalSep.setBackground(dividerColor);
        verticalSep.setOpaque(true);
        verticalSep.setPreferredSize(new Dimension(1, 60));
        dividerWrapper.add(verticalSep);

        gbc.gridx = 2;
        gbc.weightx = 0; // fixed
        gbc.insets = new Insets(0, 12, 0, 12); // space around divider so it doesn't touch neighbors
        topInfoRow.add(dividerWrapper, gbc);
        gbc.insets = new Insets(0, 0, 0, 0); // reset insets

        // === INNER RIGHT PANEL (distance + delivery) ===
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
        gbc.weightx = 0; // fixed width panel (won't steal space from innerLeft)
        topInfoRow.add(innerRightPanel, gbc);

        // === RIGHTMOST PANEL (star + rating), vertically centered and right-aligned ===
        JPanel rightmostPanel = new JPanel(new GridBagLayout());
        rightmostPanel.setOpaque(true);
        rightmostPanel.setBackground(bg);
        rightmostPanel.setPreferredSize(new Dimension(120, 80));
        rightmostPanel.setMinimumSize(new Dimension(80, 60));

        // Use GridBag to right-align and vertically center contents
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

        // add topInfoRow to header
        headerPanel.add(topInfoRow, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // === CENTER CONTENT ===
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

        // === BOTTOM: services + button ===
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
        bottomPanel.setOpaque(false);

        servicesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        servicesPanel.setOpaque(false);
        bottomPanel.add(servicesPanel, BorderLayout.CENTER);

        // Request pickup button using buttonCreator
        pickupBtn = new buttonCreator("Request Pickup", "Button.font", () -> {
            // Find the parent container with CardLayout
            Container parent = this;
            while (parent != null && !(parent.getLayout() instanceof CardLayout)) {
                parent = parent.getParent();
            }
            
            if (parent != null) {
                CardLayout cl = (CardLayout) parent.getLayout();
                cl.show(parent, "PICKUP");
            }
        });
        
        // Create a wrapper panel for the button to maintain its size and alignment
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(pickupBtn);
        
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(bottomPanel, BorderLayout.SOUTH);

        // === PLACEHOLDER STATE ===
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
            
            // Create a wrapper panel for the button to maintain its size and alignment
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
        // find any separators in the header and update their colors
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
