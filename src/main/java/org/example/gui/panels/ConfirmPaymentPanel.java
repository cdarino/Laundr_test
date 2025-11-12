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
import org.example.database.ServiceDAO;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JLabel totalTitleLabel;
    private JLabel totalAmountLabel;
    
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
    private double precomputedTotal = -1.0; // NEW: total passed from PickupPanel for accurate display

    public ConfirmPaymentPanel(String services, String quantity, String separation, String instructions, String address, double precomputedTotal) {
    this.servicesText = services;
    this.quantityText = quantity;
    this.separationText = separation;
    this.instructionsText = instructions;
    this.precomputedTotal = precomputedTotal; // store passed-in total
        
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
        
        // Replace PaymentAppItem[] paymentApps = {...} with this in createPaymentSection()
        PaymentAppItem[] paymentApps = {
            new PaymentAppItem("Select Payment App", null),
            new PaymentAppItem("Wallet", "Icons/lightmode/wallet.svg")
};
        
        paymentAppCombo = new JComboBox<>(paymentApps);
        paymentAppCombo.setRenderer(new PaymentAppRenderer());
        paymentAppCombo.setFont(UIManager.getFont("ComboBox.font"));
        paymentAppCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        paymentAppCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentAppCombo.setEnabled(false);
        paymentAppCombo.addActionListener(evt -> updatePaymentUI());
        
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboPanel.setOpaque(false);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboPanel.add(paymentAppCombo);
        
        paymentPanel.add(comboPanel);
        paymentPanel.add(Box.createVerticalStrut(12));
        
        phoneNumberLabel = new JLabel("Balance");
        phoneNumberLabel.setFont(UIManager.getFont("Label.font"));
        phoneNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        phoneNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        phoneNumberPanel.setOpaque(false);
        phoneNumberPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phoneNumberPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        phonePrefix = new JLabel(" ");
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
    boolean cashless = cashlessBtn != null && cashlessBtn.isSelected();
    boolean walletSelected = false;

    if (paymentAppCombo != null && paymentAppCombo.getSelectedIndex() > 0) {
        PaymentAppItem item = (PaymentAppItem) paymentAppCombo.getSelectedItem();
        if (item != null && "Wallet".equalsIgnoreCase(item.getName())) {
            walletSelected = true;
        }
    }

    boolean showBalanceBox = cashless && walletSelected;

    phoneNumberLabel.setVisible(showBalanceBox);
    phoneNumberPanel.setVisible(showBalanceBox);

    if (showBalanceBox) {
        try {
            Window w = SwingUtilities.getWindowAncestor(this);
            String currentUser = null;
            if (w instanceof org.example.gui.Mainframe) {
                currentUser = ((org.example.gui.Mainframe) w).getCurrentUser();
            }

            if (currentUser != null && !currentUser.isBlank()) {
                Connection conn = DBConnect.getConnection();
                if (conn != null && !conn.isClosed()) {
                    CustomerDAO cdao = new CustomerDAO(conn);
                    int custID = cdao.getCustomerId(currentUser);
                    if (custID > 0) {
                        double balance = cdao.getWalletBalance(custID);
                        phoneNumberField.setEditable(false);
                        phoneNumberField.setText(String.format("Balance: \u20B1%.2f", balance));
                    } else {
                        phoneNumberField.setEditable(false);
                        phoneNumberField.setText("Balance: N/A");
                    }
                } else {
                    phoneNumberField.setEditable(false);
                    phoneNumberField.setText("Balance: N/A");
                }
            } else {
                phoneNumberField.setEditable(false);
                phoneNumberField.setText("Balance: N/A");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            phoneNumberField.setEditable(false);
            phoneNumberField.setText("Balance: ERR");
        }
    } else {
        phoneNumberField.setEditable(true);
        phoneNumberField.setText("");
    }

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

    // Preserve incoming HTML/text for labels
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

    // --- Total display (single label, set exactly once) ---
    summaryPanel.add(Box.createVerticalStrut(12));

    totalTitleLabel = new JLabel("Total:");
    totalTitleLabel.setFont(UIManager.getFont("Label.font"));
    totalTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    summaryPanel.add(totalTitleLabel);
    summaryPanel.add(Box.createVerticalStrut(6));

    totalAmountLabel = new JLabel("\u20B10.00"); // placeholder
    try {
        totalAmountLabel.setFont(fontManager.h3());
    } catch (Exception ignored) {
        totalAmountLabel.setFont(UIManager.getFont("Label.font"));
    }
    totalAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    summaryPanel.add(totalAmountLabel);

    // Set the total exactly once here. Prefer the precomputed total from PickupPanel;
    // only compute from service names/quantity as a fallback.
    if (precomputedTotal >= 0.0) {
        totalAmountLabel.setText(String.format("\u20B1%.2f", precomputedTotal));
    } else {
        try {
            double computed = computeOrderTotal();
            totalAmountLabel.setText(String.format("\u20B1%.2f", computed));
        } catch (Exception ex) {
            totalAmountLabel.setText("\u20B1 0.00");
        }
    }

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
    // Replace the existing createAndPersistOrder() method with this transactional implementation
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

        // Determine laundromat: prefer AppState.selectedLaundromatID (fallback to first)
        int laundromatID = AppState.selectedLaundromatID;
        if (laundromatID <= 0) {
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT laundromatID FROM laundromat ORDER BY laundromatID LIMIT 1")) {
                if (rs.next()) laundromatID = rs.getInt(1);
            }
        }
        if (laundromatID <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No laundromat selected and none available in the database.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // --- Compute total from selected services (unit prices) and quantity ---
        int qty = parseQuantityFromLabel(quantityText);
        java.util.List<String> services = parseServiceNames(servicesText);

        // Resolve service IDs and unit prices for the selected laundromat
        ServiceDAO sdao = new ServiceDAO(conn);
        java.util.Map<Integer, Integer> serviceQtyMap = new java.util.LinkedHashMap<>(); // serviceID -> qty
        double sumUnitPrices = 0.0;

        for (String sname : services) {
            String normalized = sname.trim();
            if (normalized.isEmpty()) continue;

            // Prefer resolving service in the selected laundromat
            int sid = sdao.getServiceIdForName(laundromatID, normalized);

            if (sid <= 0) {
                // fallback: try name-only lookup
                sid = sdao.getServiceIdForName(-1, normalized);
            }

            if (sid <= 0) {
                // couldn't map this service name -> id in DB; log and skip
                System.err.println("ConfirmPaymentPanel: Could not find service in DB: " + normalized);
                continue;
            }

            double unitPrice = sdao.getPriceForServiceId(sid);
            if (unitPrice < 0.0) {
                System.err.println("ConfirmPaymentPanel: Could not get price for serviceID " + sid + " (" + normalized + ")");
                continue;
            }

            // accumulate unit prices (will be multiplied by qty later)
            sumUnitPrices += unitPrice;

            // store quantity for insertion into orderDetails (shared qty applies to all selected services)
            serviceQtyMap.put(sid, Math.max(1, qty));
        }

        if (serviceQtyMap.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No valid services were found to create an order. Please re-check your selection.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double total = sumUnitPrices * Math.max(1, qty);
        String instructionsPlain = extractTextFromHTML(instructionsText);
        String paymentMethod = buildPaymentMethodString();

        // --- Transactional section: create order, insert details, deduct wallet (if chosen) ---
        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // If wallet selected, check and lock wallet balance now (SELECT ... FOR UPDATE)
            if ("wallet".equalsIgnoreCase(paymentMethod)) {
                double currentBalance = 0.0;
                try (PreparedStatement ps = conn.prepareStatement("SELECT walletBalance FROM customer WHERE custID = ? FOR UPDATE")) {
                    ps.setInt(1, custID);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            currentBalance = rs.getDouble(1);
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this,
                                    "Could not read wallet balance.",
                                    "Wallet Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }

                if (currentBalance < total) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                            "Not enough funds in wallet. Current balance: \u20B1" + String.format("%.2f", currentBalance) +
                                    "\nOrder total: \u20B1" + String.format("%.2f", total),
                            "Insufficient Funds",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Create order
            OrderDAO odao = new OrderDAO(conn);
            int newId = odao.createOrder(custID, laundromatID, total, instructionsPlain, paymentMethod);
            if (newId <= 0) {
                conn.rollback();
                JOptionPane.showMessageDialog(this,
                        "Failed to create order.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // persist per-service orderDetails
            boolean detailsOk = odao.createOrderDetails(newId, serviceQtyMap);
            if (!detailsOk) {
                conn.rollback();
                JOptionPane.showMessageDialog(this,
                        "Failed to add order details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Deduct wallet if needed (done within same transaction)
            if ("wallet".equalsIgnoreCase(paymentMethod)) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE customer SET walletBalance = walletBalance - ? WHERE custID = ?")) {
                    ps.setDouble(1, total);
                    ps.setInt(2, custID);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this,
                                "Failed to deduct wallet balance.",
                                "Payment Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            // commit once everything succeeded
            conn.commit();

            // Let containers listening for "orderCreated" update if they want
            firePropertyChange("orderCreated", -1, newId);
            return true;

        } catch (Exception txEx) {
            try { conn.rollback(); } catch (Exception ignore) {}
            txEx.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unexpected error while creating order: " + txEx.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { conn.setAutoCommit(previousAutoCommit); } catch (Exception ignore) {}
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Unexpected error while creating order: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

    // Replace the existing buildPaymentMethodString() method with this
private String buildPaymentMethodString() {
    if (cashOnPickupBtn.isSelected()) return "cash_on_pickup";
    if (cashlessBtn.isSelected()) {
        if (paymentAppCombo.getSelectedIndex() <= 0) return "cashless_unselected";
        PaymentAppItem item = (PaymentAppItem) paymentAppCombo.getSelectedItem();
        String appName = item != null ? item.getName() : "wallet";
        if ("Wallet".equalsIgnoreCase(appName)) {
            return "wallet";
        }
        return appName;
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

    private void updatePaymentUI() {
    boolean cashless = cashlessBtn != null && cashlessBtn.isSelected();
    paymentAppCombo.setEnabled(cashless);

    if (!cashless) {
        // cash on pickup: keep phoneNumberField as previously used (editable but not required)
        if (phoneNumberField != null) {
            phoneNumberField.setEditable(true);
            phoneNumberField.setText("");
        }
        return;
    }

    // cashless chosen: if payment app is Wallet, display the user's wallet balance in the same text box
    PaymentAppItem sel = (PaymentAppItem) paymentAppCombo.getSelectedItem();
    String appName = (sel != null && sel.getName() != null) ? sel.getName() : "";
    if ("Wallet".equalsIgnoreCase(appName)) {
        // show balance in the phoneNumberField (keeps layout; field is read-only)
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                CustomerDAO cdao = new CustomerDAO(conn);
                Window w = SwingUtilities.getWindowAncestor(this);
                String currentUser = null;
                if (w instanceof org.example.gui.Mainframe) {
                    currentUser = ((org.example.gui.Mainframe) w).getCurrentUser();
                }
                if (currentUser != null && !currentUser.isBlank()) {
                    int custID = cdao.getCustomerId(currentUser);
                    if (custID > 0) {
                        double balance = cdao.getWalletBalance(custID);
                        if (phoneNumberField != null) {
                            phoneNumberField.setEditable(false);
                            phoneNumberField.setText(String.format("\u20B1%.2f", balance));
                        }
                    } else {
                        if (phoneNumberField != null) {
                            phoneNumberField.setEditable(false);
                            phoneNumberField.setText("N/A");
                        }
                    }
                } else {
                    if (phoneNumberField != null) {
                        phoneNumberField.setEditable(false);
                        phoneNumberField.setText("N/A");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (phoneNumberField != null) {
                phoneNumberField.setEditable(false);
                phoneNumberField.setText("ERR");
            }
        }
    } else {
        // another cashless option (shouldn't happen because we only have Wallet) — keep read-only but blank
        if (phoneNumberField != null) {
            phoneNumberField.setEditable(false);
            phoneNumberField.setText("");
        }
    }
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
                    "Please select a payment app (Wallet).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Phone number is no longer required; wallet balance will be validated separately
    }

    return true;
}

// Add to ConfirmPaymentPanel class
/**
 * Compute the pending order total from the preserved servicesText and quantityText
 * Uses ServiceDAO to resolve service base prices.
 */
private double computeOrderTotal() throws Exception {
    int qty = 1;
    // extract integer quantity from quantityText, fallback to 1
    if (quantityText != null) {
        String digits = quantityText.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            try { qty = Math.max(1, Integer.parseInt(digits)); } catch (NumberFormatException ignored) {}
        }
    }

    java.util.List<String> services = parseServiceNames(servicesText);
    if (services == null || services.isEmpty()) return 0.0;

    Connection conn = DBConnect.getConnection();
    if (conn == null || conn.isClosed()) throw new Exception("No DB connection");

    org.example.database.ServiceDAO sdao = new org.example.database.ServiceDAO(conn);
    double sumUnitPrices = 0.0;
    // Try to prefer selected laundromat if AppState.selectedLaundromatID is set
    int laundromatID = -1;
    try {
        laundromatID = AppState.selectedLaundromatID;
    } catch (Exception ignored) { laundromatID = -1; }

    for (String name : services) {
        String normalized = name.trim();
        if (normalized.isEmpty()) continue;

        int sid = sdao.getServiceIdForName(laundromatID > 0 ? laundromatID : -1, normalized);
        if (sid <= 0) {
            // fallback: name-only
            sid = sdao.getServiceIdForName(-1, normalized);
        }
        if (sid <= 0) continue;

        double unitPrice = sdao.getPriceForServiceId(sid);
        if (unitPrice >= 0.0) sumUnitPrices += unitPrice;
    }

    return sumUnitPrices * Math.max(1, qty);
}

    /** Parse the servicesText (HTML/label) into a list of service names (best-effort). */
private List<String> parseServiceNames(String servicesHtml) {
    List<String> out = new ArrayList<>();
    if (servicesHtml == null) return out;
    String plain = servicesHtml.replaceAll("<[^>]*>", "\n");
    plain = plain.replace("•", "\n").replace("·", "\n");
    String[] lines = plain.split("[\\r\\n]+");
    for (String line : lines) {
        String l = line.trim();
        l = l.replaceAll("^[\\u2022\\u2023\\-\\*\\s]+", "").trim();
        if (!l.isEmpty()) out.add(l);
    }
    return out;
}

/** Parse quantity integer from the HTML label passed in quantityText (returns 1 if none). */
private int parseQuantityFromLabel(String qtyLabelHtml) {
    if (qtyLabelHtml == null) return 1;
    String plain = qtyLabelHtml.replaceAll("<[^>]*>", " ").replace("\u00A0", " ").trim();
    Matcher m = Pattern.compile("(\\d+)").matcher(plain);
    if (m.find()) {
        try { return Integer.parseInt(m.group(1)); }
        catch (NumberFormatException ignored) {}
    }
    return 1;
}

/** Count selected services from the servicesText (best-effort). */
private int parseSelectedServiceCount() {
    List<String> s = parseServiceNames(servicesText);
    return s.size() > 0 ? s.size() : 1;
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