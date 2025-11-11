package org.example.gui.panels;

import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.fonts.fontLoader;
import org.example.gui.utils.fonts.fontManager;
import org.example.session.AppState;
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.example.session.AppState;
import org.example.database.DBConnect;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConfirmPaymentPanel extends JPanel {
    private static final Color FIXED_BACKGROUND = new Color(245, 245, 245);
    
    private static final double PAYMENT_RATIO = 0.80;
    private static final double SUMMARY_RATIO = 0.20;
    
    private static final int SECTION_GAP = 20;
    private static final int PADDING = 20;
    
    private JPanel mainContainer;
    private roundedPanel paymentSection, summarySection;
    
    // Payment section components
    private JLabel paymentTitleLabel;
    private JLabel paymentMethodLabel, payViaLabel, phoneNumberLabel, addressTextLabel;
    private JRadioButton cashlessBtn, cashOnPickupBtn;
    private ButtonGroup paymentMethodGroup;
    private JComboBox<PaymentAppItem> paymentAppCombo;
    private JPanel phoneNumberPanel;
    private JLabel phonePrefix;
    private JTextField phoneNumberField;
    private JLabel addressIconLabel, addressValueLabel;
    
    // Summary section components
    private JLabel summaryTitleLabel;
    private JLabel selectedServiceLabel, quantitySummaryLabel, separationSummaryLabel;
    private JTextArea instructionsSummaryArea;
    private JPanel summaryPanel;
    private JLabel instructionsHeaderLabel;
    
    // Bottom buttons
    private buttonCreator goBackBtn, confirmPaymentBtn;
    
    private boolean isInitialized = false;
    
    // Preserved data from PickupPanel
    private String servicesText;
    private String quantityText;
    private String separationText;
    private String instructionsText;
    private String userAddress;
    private String loggedInUsername;

    public ConfirmPaymentPanel(String services, String quantity, String separation, String instructions, String address) {
        this.servicesText = services;
        this.quantityText = quantity;
        this.separationText = separation;
        this.instructionsText = instructions;
        
        // Fetch address from database
        this.userAddress = fetchUserAddress();
        if (this.userAddress == null || this.userAddress.trim().isEmpty()) {
            this.userAddress = address != null ? address : "No address on file";
        }
        
        fontLoader.loadFonts();
        
        setLayout(new BorderLayout());
        setBackground(FIXED_BACKGROUND);
        
        mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(true);
        mainContainer.setBorder(new EmptyBorder(PADDING, 100, PADDING, PADDING));
        
        paymentSection = createPaymentSection();
        summarySection = createSummarySection();
        
        layoutSections();
        
        JPanel bottomPanel = createBottomButtons();
        
        add(mainContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        isInitialized = true;
        
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName())) {
                    SwingUtilities.invokeLater(() -> updateThemeColors());
                }
            }
        });
        
        updateThemeColors();
    }
    
    private String fetchUserAddress() {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                // TODO: Get logged in username from session/auth manager
                // CustomerDAO dao = new CustomerDAO(conn);
                // return dao.getAddress(loggedInUsername);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void layoutSections() {
        mainContainer.removeAll();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        
        gbc.gridx = 0;
        gbc.weightx = PAYMENT_RATIO;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, SECTION_GAP);
        mainContainer.add(paymentSection, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.20;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(Box.createHorizontalGlue(), gbc);
        
        gbc.gridx = 2;
        gbc.weightx = SUMMARY_RATIO;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(summarySection, gbc);
        
        mainContainer.revalidate();
        mainContainer.repaint();
    }
    
    private roundedPanel createPaymentSection() {
        JPanel outerContainer = new JPanel(new BorderLayout(0, 6));
        outerContainer.setOpaque(true);
        
        paymentTitleLabel = new JLabel("Confirm Payment");
        paymentTitleLabel.setFont(fontManager.h2());
        paymentTitleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        outerContainer.add(paymentTitleLabel, BorderLayout.NORTH);
        
        roundedPanel section = new roundedPanel(18);
        section.setLayout(new BorderLayout(10, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(18, getAccentBorderColor(), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setOpaque(true);
        paymentPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        paymentMethodLabel = new JLabel("Payment Method");
        paymentMethodLabel.setFont(UIManager.getFont("Label.font"));
        paymentMethodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(paymentMethodLabel);
        paymentPanel.add(Box.createVerticalStrut(10));
        
        JPanel methodRadioPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        methodRadioPanel.setOpaque(false);
        methodRadioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodRadioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        paymentMethodGroup = new ButtonGroup();
        cashlessBtn = new JRadioButton("Cashless");
        cashOnPickupBtn = new JRadioButton("Cash on Pickup");
        
        cashlessBtn.setFont(UIManager.getFont("Label.font"));
        cashOnPickupBtn.setFont(UIManager.getFont("Label.font"));
        cashlessBtn.setOpaque(false);
        cashOnPickupBtn.setOpaque(false);
        
        paymentMethodGroup.add(cashlessBtn);
        paymentMethodGroup.add(cashOnPickupBtn);
        
        methodRadioPanel.add(cashlessBtn);
        methodRadioPanel.add(cashOnPickupBtn);
        
        paymentPanel.add(methodRadioPanel);
        paymentPanel.add(Box.createVerticalStrut(20));
        
        payViaLabel = new JLabel("Pay via");
        payViaLabel.setFont(UIManager.getFont("Label.font"));
        payViaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(payViaLabel);
        paymentPanel.add(Box.createVerticalStrut(8));
        
        PaymentAppItem[] paymentApps = {
            new PaymentAppItem("Select Payment App", null),
            new PaymentAppItem("GCash", "Icons/apps/gcash.svg"),
            new PaymentAppItem("Maya", "Icons/apps/paymaya.svg"),
            new PaymentAppItem("PayPal", "Icons/apps/paypal.svg"),
            new PaymentAppItem("ShopeePay", "Icons/apps/shopeepay.svg"),
            new PaymentAppItem("GrabPay", "Icons/apps/grabpay.svg"),
//            new PaymentAppItem("Digital Wallet", "Icons/lightmode/wallet.svg")
        };
        
        paymentAppCombo = new JComboBox<>(paymentApps);
        paymentAppCombo.setRenderer(new PaymentAppRenderer());
        paymentAppCombo.setFont(UIManager.getFont("ComboBox.font"));
        paymentAppCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        paymentAppCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentAppCombo.setEnabled(false);
        
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboPanel.setOpaque(false);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboPanel.add(paymentAppCombo);
        
        paymentPanel.add(comboPanel);
        paymentPanel.add(Box.createVerticalStrut(12));
        
        phoneNumberLabel = new JLabel("Phone Number");
        phoneNumberLabel.setFont(UIManager.getFont("Label.font"));
        phoneNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        phoneNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        phoneNumberPanel.setOpaque(false);
        phoneNumberPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phoneNumberPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        phonePrefix = new JLabel("+63 ");
        phonePrefix.setFont(UIManager.getFont("TextField.font"));
        phonePrefix.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 2)
        ));
        phonePrefix.setOpaque(true);
        phonePrefix.setBackground(UIManager.getColor("TextField.background"));
        
        phoneNumberField = new JTextField(12);
        phoneNumberField.setFont(UIManager.getFont("TextField.font"));
        phoneNumberField.setPreferredSize(new Dimension(180, 32));
        phoneNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
            new EmptyBorder(5, 2, 5, 8)
        ));
        
        phoneNumberPanel.add(phonePrefix);
        phoneNumberPanel.add(phoneNumberField);
        phoneNumberPanel.setVisible(false);
        
        paymentPanel.add(phoneNumberLabel);
        phoneNumberLabel.setVisible(false);
        paymentPanel.add(Box.createVerticalStrut(8));
        paymentPanel.add(phoneNumberPanel);
        paymentPanel.add(Box.createVerticalStrut(20));
        
        addressTextLabel = new JLabel("Address");
        addressTextLabel.setFont(UIManager.getFont("Label.font"));
        addressTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(addressTextLabel);
        paymentPanel.add(Box.createVerticalStrut(10));
        
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addressPanel.setOpaque(false);
        addressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        Icon locationIcon = iconCreator.getIcon(
            isDarkTheme() ? "Icons/darkmode/addressDarkMode.svg" : "Icons/lightmode/address.svg",
            20, 20
        );
        addressIconLabel = new JLabel(locationIcon);
        addressPanel.add(addressIconLabel);
        
        addressValueLabel = new JLabel("<html><body style='width:90%;'>" + userAddress + "</body></html>");
        addressValueLabel.setFont(UIManager.getFont("Label.font"));
        addressValueLabel.setVerticalAlignment(SwingConstants.TOP);
        addressPanel.add(addressValueLabel);
        
        paymentPanel.add(addressPanel);
        paymentPanel.add(Box.createVerticalGlue());
        
        section.add(paymentPanel, BorderLayout.CENTER);
        
        outerContainer.add(section, BorderLayout.CENTER);
        
        roundedPanel wrapper = new roundedPanel(0);
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.add(outerContainer, BorderLayout.CENTER);
        
        cashlessBtn.addActionListener(e -> {
            paymentAppCombo.setEnabled(true);
            paymentAppCombo.setSelectedIndex(0);
            updatePhoneNumberVisibility();
        });
        
        cashOnPickupBtn.addActionListener(e -> {
            paymentAppCombo.setEnabled(false);
            paymentAppCombo.setSelectedIndex(0);
            phoneNumberLabel.setVisible(false);
            phoneNumberPanel.setVisible(false);
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });
        
        paymentAppCombo.addActionListener(e -> updatePhoneNumberVisibility());
        
        return wrapper;
    }
    
    private void updatePhoneNumberVisibility() {
        boolean showPhone = cashlessBtn.isSelected() && paymentAppCombo.getSelectedIndex() > 0;
        phoneNumberLabel.setVisible(showPhone);
        phoneNumberPanel.setVisible(showPhone);
        
        Container parent = phoneNumberPanel.getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private roundedPanel createSummarySection() {
        JPanel outerContainer = new JPanel(new BorderLayout(0, 6));
        outerContainer.setOpaque(true);
        
        summaryTitleLabel = new JLabel("Order Summary");
        summaryTitleLabel.setFont(fontManager.h2());
        summaryTitleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        outerContainer.add(summaryTitleLabel, BorderLayout.NORTH);
        
        roundedPanel section = new roundedPanel(18);
        section.setLayout(new BorderLayout(10, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(18, getAccentBorderColor(), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(true);
        summaryPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        selectedServiceLabel = createSummaryLabel(servicesText);
        quantitySummaryLabel = createSummaryLabel(quantityText);
        separationSummaryLabel = createSummaryLabel(separationText);
        
        summaryPanel.add(selectedServiceLabel);
        summaryPanel.add(Box.createVerticalStrut(15));
        summaryPanel.add(quantitySummaryLabel);
        summaryPanel.add(Box.createVerticalStrut(15));
        summaryPanel.add(separationSummaryLabel);
        summaryPanel.add(Box.createVerticalStrut(15));
        
        instructionsHeaderLabel = new JLabel("Instructions:");
        instructionsHeaderLabel.setFont(UIManager.getFont("Label.font"));
        instructionsHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(instructionsHeaderLabel);
        summaryPanel.add(Box.createVerticalStrut(5));
        
        instructionsSummaryArea = new JTextArea();
        instructionsSummaryArea.setText(extractTextFromHTML(instructionsText));
        instructionsSummaryArea.setLineWrap(true);
        instructionsSummaryArea.setWrapStyleWord(true);
        instructionsSummaryArea.setEditable(false);
        instructionsSummaryArea.setFont(UIManager.getFont("TextArea.font"));
        instructionsSummaryArea.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(10, new Color(200, 200, 200), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        instructionsSummaryArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JScrollPane instructionsSummaryScroll = new JScrollPane(instructionsSummaryArea);
        instructionsSummaryScroll.setBorder(null);
        instructionsSummaryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instructionsSummaryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        instructionsSummaryScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsSummaryScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        instructionsSummaryScroll.setPreferredSize(new Dimension(0, 100));
        
        summaryPanel.add(instructionsSummaryScroll);
        summaryPanel.add(Box.createVerticalGlue());
        
        JScrollPane summaryScroll = new JScrollPane(summaryPanel);
        summaryScroll.setOpaque(true);
        summaryScroll.getViewport().setOpaque(true);
        summaryScroll.setBorder(null);
        summaryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        summaryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(summaryScroll, BorderLayout.CENTER);
        
        outerContainer.add(section, BorderLayout.CENTER);
        
        roundedPanel wrapper = new roundedPanel(0);
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.add(outerContainer, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIManager.getFont("Label.font"));
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createBottomButtons() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(true);
        bottomPanel.setBorder(new EmptyBorder(10, PADDING, PADDING, PADDING));
        
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtonPanel.setOpaque(false);
        
        goBackBtn = new buttonCreator("Go Back", "Button.font", () -> {
            Container parent = getParent();
            if (parent instanceof JPanel) {
                ((CardLayout) parent.getLayout()).show(parent, "PICKUP");
            }
        });
        styleButton(goBackBtn);
        
        leftButtonPanel.add(goBackBtn);
        
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtonPanel.setOpaque(false);
        
        confirmPaymentBtn = new buttonCreator("Confirm Payment", "Button.font", () -> {
            if (validatePayment()) {
                // NEW: Create the order in DB before navigating
                if (!createAndPersistOrder()) {
                    // If creation fails, stop here and show error (already shown inside)
                    return;
                }
                JOptionPane.showMessageDialog(this,
                    "Payment confirmed! Your order has been placed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                Container parent = getParent();
                if (parent instanceof JPanel) {
                    ((CardLayout) parent.getLayout()).show(parent, "ORDERS");
                }
            }
        });
        styleButton(confirmPaymentBtn);
        
        rightButtonPanel.add(confirmPaymentBtn);
        
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);
        
        return bottomPanel;
    }

    // NEW: persist order using same customer resolution as Orders panel
    private boolean createAndPersistOrder() {
        try {
            // Resolve current username from the top-level window (Mainframe)
            Window w = SwingUtilities.getWindowAncestor(this);
            String currentUser = null;
            if (w instanceof org.example.gui.Mainframe) {
                currentUser = ((org.example.gui.Mainframe) w).getCurrentUser();
            }
            if (currentUser == null || currentUser.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "You must be logged in to place an order.",
                        "Not Logged In",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            Connection conn = DBConnect.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this,
                        "No database connection.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Get custID using the same DAO logic Orders uses
            CustomerDAO cdao = new CustomerDAO(conn);
            int custID = cdao.getCustomerId(currentUser);
            if (custID <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Could not resolve your customer profile.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Pick a laundromatID — if you track selection elsewhere, replace this lookup.
            // prefer selected laundromat from AppState
int laundromatID = AppState.selectedLaundromatID;

// if not set, fallback to previous approach: pick the first laundromat in the DB
if (laundromatID <= 0) {
    try (Connection c = DBConnect.getConnection()) {
        if (c != null && !c.isClosed()) {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT laundromatID FROM laundromat ORDER BY laundromatID LIMIT 1")) {
                if (rs.next()) laundromatID = rs.getInt(1);
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

if (laundromatID <= 0) {
    JOptionPane.showMessageDialog(this,
            "No laundromat selected and none available in the database.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    return false;
}

            // Compute a simple total from the quantity text (keeps your UI unchanged)
            double total = 0.0;
            try {
                if (quantityText != null) {
                    String digits = quantityText.replaceAll("[^0-9.]", "");
                    if (!digits.isEmpty()) {
                        total = Double.parseDouble(digits) * 50.0; // nominal price per unit
                    }
                }
            } catch (Exception ignored) {}

            String instructionsPlain = extractTextFromHTML(instructionsText);
            String paymentMethod = buildPaymentMethodString();

            OrderDAO odao = new OrderDAO(conn);
            int newId = odao.createOrder(custID, laundromatID, total, instructionsPlain, paymentMethod);
            if (newId <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Failed to create order.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Let containers listening for "orderCreated" update if they want
            firePropertyChange("orderCreated", -1, newId);
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error while creating order: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String buildPaymentMethodString() {
        if (cashOnPickupBtn.isSelected()) return "cash_on_pickup";
        if (cashlessBtn.isSelected()) {
            if (paymentAppCombo.getSelectedIndex() <= 0) return "cashless_unselected";
            PaymentAppItem item = (PaymentAppItem) paymentAppCombo.getSelectedItem();
            String phone = phoneNumberField.getText().trim();
            return item.getName() + (phone.isEmpty() ? "" : " (" + phone + ")");
        }
        return "none_selected";
    }
    
    private void styleButton(buttonCreator btn) {
        Font fredokaBold = getFredokaBold(14f);
        btn.setCustomFont(fredokaBold);
        btn.enableAutoScaleToWidth(0.90f, 12f, 26f);
        
        Dimension ph = btn.getPreferredSize();
        int fixedHeight = Math.max(ph.height + 20, ph.height);
        btn.setPreferredSize(new Dimension(ph.width, fixedHeight));
        btn.setMinimumSize(new Dimension(0, fixedHeight));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));
        
        final Color limeHover = UIManager.getColor("Sidebar.hoverBackground") != null
                ? UIManager.getColor("Sidebar.hoverBackground")
                : new Color(0xDAEC73);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(limeHover);
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                Color bgBtn = UIManager.getColor("Button.background");
                btn.setBackground(bgBtn);
                btn.repaint();
            }
        });
        
        SwingUtilities.invokeLater(btn::rescaleNow);
    }
    
    private Font getFredokaBold(float size) {
        String[] candidates = { "Fredoka", "Fredoka Medium", "Fredoka One" };
        for (String name : candidates) {
            Font f = new Font(name, Font.BOLD, Math.round(size));
            if (!"Dialog".equalsIgnoreCase(f.getFamily())) {
                return f.deriveFont(Font.BOLD, size);
            }
        }
        return new Font("Dialog", Font.BOLD, Math.round(size));
    }
    
    private boolean validatePayment() {
        if (!cashlessBtn.isSelected() && !cashOnPickupBtn.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please select a payment method.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (cashlessBtn.isSelected()) {
            if (paymentAppCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Please select a payment app for cashless payment.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            String phoneNumber = phoneNumberField.getText().trim();
            if (phoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter your phone number for cashless payment.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (!phoneNumber.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid 10-digit phone number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    private String extractTextFromHTML(String html) {
        if (html == null) return "None";
        String text = html.replaceAll("<[^>]*>", "");
        text = text.replace("&bull;", "\u2022");
        text = text.replace("&nbsp;", " ");
        return text.trim().isEmpty() ? "None" : text.trim();
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
    
    private boolean isDarkTheme() {
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) bg = UIManager.getColor("background");
        if (bg == null) bg = getBackground();
        if (bg == null) return false;
        double luminance = (0.299 * bg.getRed()) + (0.587 * bg.getGreen()) + (0.114 * bg.getBlue());
        return luminance < 128;
    }
    
    private Color getAdaptiveForegroundColor() {
        return isDarkTheme() ? Color.WHITE : Color.BLACK;
    }
    
    private void updateThemeColors() {
        if (!isInitialized) return;
        
        boolean isDark = FlatLaf.isLafDark();
        Color foregroundColor = getAdaptiveForegroundColor();
        
        // Update title labels
        if (paymentTitleLabel != null) {
            paymentTitleLabel.setFont(fontManager.h2());
            paymentTitleLabel.setForeground(foregroundColor);
        }
        if (summaryTitleLabel != null) {
            summaryTitleLabel.setFont(fontManager.h2());
            summaryTitleLabel.setForeground(foregroundColor);
        }
        
        // Update payment section labels
        if (paymentMethodLabel != null) paymentMethodLabel.setForeground(foregroundColor);
        if (payViaLabel != null) payViaLabel.setForeground(foregroundColor);
        if (phoneNumberLabel != null) phoneNumberLabel.setForeground(foregroundColor);
        if (addressTextLabel != null) addressTextLabel.setForeground(foregroundColor);
        if (addressValueLabel != null) addressValueLabel.setForeground(foregroundColor);
        
        // Update radio buttons
        if (cashlessBtn != null) cashlessBtn.setForeground(foregroundColor);
        if (cashOnPickupBtn != null) cashOnPickupBtn.setForeground(foregroundColor);
        
        // Update summary section labels
        if (selectedServiceLabel != null) selectedServiceLabel.setForeground(foregroundColor);
        if (quantitySummaryLabel != null) quantitySummaryLabel.setForeground(foregroundColor);
        if (separationSummaryLabel != null) separationSummaryLabel.setForeground(foregroundColor);
        if (instructionsHeaderLabel != null) instructionsHeaderLabel.setForeground(foregroundColor);
        
        Color textFieldBackground = UIManager.getColor("TextField.background");
        Color textFieldForeground = UIManager.getColor("TextField.foreground");
        Color borderColor = UIManager.getColor("Component.borderColor");
        if (borderColor == null) {
            borderColor = isDark ? new Color(70, 70, 70) : new Color(200, 200, 200);
        }
        
        if (paymentAppCombo != null) {
            paymentAppCombo.setForeground(textFieldForeground);
        }
        
        if (phonePrefix != null) {
            phonePrefix.setBackground(textFieldBackground);
            phonePrefix.setForeground(textFieldForeground);
            phonePrefix.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 0, borderColor),
                new EmptyBorder(5, 8, 5, 2)
            ));
        }
        
        if (phoneNumberField != null) {
            phoneNumberField.setBackground(textFieldBackground);
            phoneNumberField.setForeground(textFieldForeground);
            phoneNumberField.setCaretColor(textFieldForeground);
            phoneNumberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, borderColor),
                new EmptyBorder(5, 2, 5, 8)
            ));
        }
        
        if (instructionsSummaryArea != null) {
            instructionsSummaryArea.setBackground(textFieldBackground);
            instructionsSummaryArea.setForeground(textFieldForeground);
            instructionsSummaryArea.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(10, borderColor, 1),
                new EmptyBorder(8, 8, 8, 8)
            ));
        }
        
        if (addressIconLabel != null) {
            Icon locationIcon = iconCreator.getIcon(
                isDark ? "Icons/darkmode/addressDarkMode.svg" : "Icons/lightmode/address.svg",
                20, 20
            );
            addressIconLabel.setIcon(locationIcon);
        }
        
        updateSectionBorders();
        
        repaint();
    }
    
    private void updateSectionBorders() {
        Color accent = getAccentBorderColor();
        updatePanelBorder(paymentSection, accent);
        updatePanelBorder(summarySection, accent);
    }
    
    private void updatePanelBorder(roundedPanel panel, Color accent) {
        if (panel == null) return;
        Component[] comps = panel.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                JPanel jp = (JPanel) c;
                Component[] innerComps = jp.getComponents();
                for (Component ic : innerComps) {
                    if (ic instanceof roundedPanel) {
                        roundedPanel rp = (roundedPanel) ic;
                        rp.setBorder(BorderFactory.createCompoundBorder(
                            new roundedBorder(18, accent, 2),
                            new EmptyBorder(15, 15, 15, 15)
                        ));
                    }
                }
            }
        }
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(FIXED_BACKGROUND);
        
        if (isInitialized) {
            updateThemeColors();
            if (goBackBtn != null) goBackBtn.rescaleNow();
            if (confirmPaymentBtn != null) confirmPaymentBtn.rescaleNow();
        }
    }
    
    private static class PaymentAppItem {
        private String name;
        private String iconPath;
        
        public PaymentAppItem(String name, String iconPath) {
            this.name = name;
            this.iconPath = iconPath;
        }
        
        public String getName() {
            return name;
        }
        
        public String getIconPath() {
            return iconPath;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    private static class PaymentAppRenderer extends JLabel implements ListCellRenderer<PaymentAppItem> {
        
        public PaymentAppRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
            setBorder(new EmptyBorder(5, 8, 5, 8));
        }
        
        @Override
        public Component getListCellRendererComponent(
                JList<? extends PaymentAppItem> list,
                PaymentAppItem value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            
            if (value == null) {
                setText("");
                setIcon(null);
                return this;
            }
            
            setText(value.getName());
            
            if (value.getIconPath() != null) {
                try {
                    Icon icon = iconCreator.getIcon(value.getIconPath(), 20, 20);
                    setIcon(icon);
                    setIconTextGap(8);
                } catch (Exception e) {
                    setIcon(null);
                }
            } else {
                setIcon(null);
            }
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setFont(list.getFont());
            
            return this;
        }
    }
}