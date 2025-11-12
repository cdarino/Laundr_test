package org.example.gui.panels;

import org.example.gui.laundromats.ServiceCard;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.fonts.fontLoader;
import org.example.gui.utils.fonts.fontManager;
import org.example.database.DBConnect;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.sql.SQLException;

import org.example.database.ServiceDAO;
import org.example.service.Service;
import org.example.service.ServiceFactory;
import java.util.Set;
import java.util.LinkedHashSet;


public class PickupPanel extends JPanel {
    private static final Color FIXED_BACKGROUND = new Color(245, 245, 245);
    
    private static final double SERVICES_RATIO = 0.25;
    private static final double DETAILS_RATIO = 0.55;
    private static final double SUMMARY_RATIO = 0.20;
    
    private static final int SECTION_GAP = 20;
    private static final int PADDING = 20;
    
    private JPanel mainContainer;
    private roundedPanel servicesSection, detailsSection, summarySection;
    private JPanel servicesPanel, detailsPanel, summaryPanel;
    
    private JLabel serviceTitleLabel;
    private List<ServiceButton> serviceButtons = new ArrayList<>();
    private Set<String> selectedServices = new LinkedHashSet<>();
    
    private JLabel detailsTitleLabel;
    private JRadioButton kiloBtn, articleBtn, yesBtn, noBtn;
    private ButtonGroup serviceTypeGroup, colorSeparationGroup;
    private JTextField quantityField;
    private JTextArea instructionsArea;
    private JLabel separationLabel, instructionsTextLabel, instructionsHeaderLabel;
    
    private JLabel summaryTitleLabel;
    private JLabel selectedServiceLabel, quantitySummaryLabel, separationSummaryLabel;
    private JTextArea instructionsSummaryArea;
    private JLabel totalTitleLabel;
    private JLabel totalAmountLabel;
    
    private buttonCreator goBackBtn, confirmBtn;
    
    private boolean isInitialized = false;
    
    public PickupPanel() {
        fontLoader.loadFonts();
        
        setLayout(new BorderLayout());
        setBackground(FIXED_BACKGROUND);
        
        mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(true);
        mainContainer.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        initializeSections();
        
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
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutSections();
            }
        });
        
        updateThemeColors();
    }
    
    private void initializeSections() {
        servicesSection = createServicesSection();
        detailsSection = createDetailsSection();
        summarySection = createSummarySection();
    }
    
    private void layoutSections() {
        if (!isInitialized) return;
        
        mainContainer.removeAll();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, SECTION_GAP);
        
        gbc.gridx = 0;
        gbc.weightx = SERVICES_RATIO;
        gbc.weighty = 1.0;
        mainContainer.add(servicesSection, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = DETAILS_RATIO;
        mainContainer.add(detailsSection, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = SUMMARY_RATIO;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(summarySection, gbc);
        
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private roundedPanel createServicesSection() {
        JPanel outerContainer = new JPanel(new BorderLayout(0, 6));
        outerContainer.setOpaque(true);

        serviceTitleLabel = new JLabel("Select Services");
        serviceTitleLabel.setFont(fontManager.h2());
        serviceTitleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        outerContainer.add(serviceTitleLabel, BorderLayout.NORTH);

        // main section panel
        roundedPanel section = new roundedPanel(18);
        section.setLayout(new BorderLayout(10, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(18, getAccentBorderColor(), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Grid panel for service buttons
        servicesPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        servicesPanel.setOpaque(true);

        // --- Fetch services dynamically from the database ---
        List<Service> services = new ArrayList<>();
        Connection conn = DBConnect.getConnection(); // DO NOT close this connection
        if (conn != null) {
            try {
                ServiceDAO serviceDAO = new ServiceDAO(conn);
                services = serviceDAO.getAllServices();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /*if (services.isEmpty()) {
            System.out.println("No services found in DB, loading defaults...");
            services = List.of(
                    ServiceFactory.create("washandfold"),
                    ServiceFactory.create("dryclean"),
                    ServiceFactory.create("pressonly")
            );
        }*/
        // If this method can be called more than once, clear previous buttons to avoid duplicated UI and state.
        serviceButtons.clear();
        servicesPanel.removeAll();
        for (Service s : services) {
        // pass the base price to the button and keep a reference for theme updates
            ServiceButton card = new ServiceButton(s.getName(), s.getIconPath(), s.basePrice());
            servicesPanel.add(card);
            serviceButtons.add(card);
}

        // Scroll pane for services
        JScrollPane scrollPane = new JScrollPane(servicesPanel);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(scrollPane, BorderLayout.CENTER);

        outerContainer.add(section, BorderLayout.CENTER);

        // Wrapper panel
        roundedPanel wrapper = new roundedPanel(0);
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.add(outerContainer, BorderLayout.CENTER);

        return wrapper;
    }

    private roundedPanel createDetailsSection() {
        JPanel outerContainer = new JPanel(new BorderLayout(0, 6));
        outerContainer.setOpaque(true);
        
        detailsTitleLabel = new JLabel("Details");
        detailsTitleLabel.setFont(fontManager.h2());
        detailsTitleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        outerContainer.add(detailsTitleLabel, BorderLayout.NORTH);
        
        roundedPanel section = new roundedPanel(18);
        section.setLayout(new BorderLayout(10, 10));
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(18, getAccentBorderColor(), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(true);
        detailsPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        JPanel serviceTypeRadioPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        serviceTypeRadioPanel.setOpaque(false);
        serviceTypeRadioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceTypeRadioPanel.setMaximumSize(new Dimension(450, 30));
        
        serviceTypeGroup = new ButtonGroup();
        kiloBtn = new JRadioButton("Loads");
        articleBtn = new JRadioButton("Articles of Clothing");
        
        kiloBtn.setFont(UIManager.getFont("Label.font"));
        articleBtn.setFont(UIManager.getFont("Label.font"));
        kiloBtn.setOpaque(false);
        articleBtn.setOpaque(false);
        
        serviceTypeGroup.add(kiloBtn);
        serviceTypeGroup.add(articleBtn);
        
        serviceTypeRadioPanel.add(kiloBtn);
        serviceTypeRadioPanel.add(articleBtn);
        
        detailsPanel.add(serviceTypeRadioPanel);
        detailsPanel.add(Box.createVerticalStrut(12));
        
        JPanel quantityFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityFieldPanel.setOpaque(true);
        quantityFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        quantityField = new JTextField(15);
        quantityField.setFont(UIManager.getFont("TextField.font"));
        quantityField.setPreferredSize(new Dimension(250, 32));
        quantityFieldPanel.add(quantityField);
        
        detailsPanel.add(quantityFieldPanel);
        detailsPanel.add(Box.createVerticalStrut(20));
        
        separationLabel = new JLabel("Separate Colored & Non-Colored?");
        separationLabel.setFont(UIManager.getFont("Label.font"));
        separationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(separationLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        
        JPanel separationRadioPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        separationRadioPanel.setOpaque(false);
        separationRadioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        separationRadioPanel.setMaximumSize(new Dimension(450, 30));
        
        colorSeparationGroup = new ButtonGroup();
        yesBtn = new JRadioButton("Yes");
        noBtn = new JRadioButton("No");
        
        yesBtn.setFont(UIManager.getFont("Label.font"));
        noBtn.setFont(UIManager.getFont("Label.font"));
        yesBtn.setOpaque(false);
        noBtn.setOpaque(false);
        
        colorSeparationGroup.add(yesBtn);
        colorSeparationGroup.add(noBtn);
        
        separationRadioPanel.add(yesBtn);
        separationRadioPanel.add(noBtn);
        
        detailsPanel.add(separationRadioPanel);
        detailsPanel.add(Box.createVerticalStrut(20));
        
        instructionsTextLabel = new JLabel("Custom Instructions:");
        instructionsTextLabel.setFont(UIManager.getFont("Label.font"));
        instructionsTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(instructionsTextLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        
        instructionsArea = new JTextArea(5, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setFont(UIManager.getFont("TextArea.font"));
        instructionsArea.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(10, new Color(200, 200, 200), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        instructionsScroll.setBorder(null);
        instructionsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        instructionsScroll.setPreferredSize(new Dimension(0, 130));
        instructionsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instructionsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsPanel.add(instructionsScroll);
        detailsPanel.add(Box.createVerticalGlue());
        
        JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setOpaque(true);
        detailsScroll.getViewport().setOpaque(true);
        detailsScroll.setBorder(null);
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(detailsScroll, BorderLayout.CENTER);
        
        outerContainer.add(section, BorderLayout.CENTER);
        
        roundedPanel wrapper = new roundedPanel(0);
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.add(outerContainer, BorderLayout.CENTER);
        
        ActionListener updateSummary = e -> updateOrderSummary();
        kiloBtn.addActionListener(updateSummary);
        articleBtn.addActionListener(updateSummary);
        yesBtn.addActionListener(updateSummary);
        noBtn.addActionListener(updateSummary);
        
        javax.swing.event.DocumentListener docListener = new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
        };
        quantityField.getDocument().addDocumentListener(docListener);
        instructionsArea.getDocument().addDocumentListener(docListener);
        
        return wrapper;
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
        
        selectedServiceLabel = createSummaryLabel("Availed Service/s:\nNone");
        quantitySummaryLabel = createSummaryLabel("Quantity:\n0");
        separationSummaryLabel = createSummaryLabel("Separation:\nDo Not Separate");
        
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
        instructionsSummaryArea.setText("None");
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

        // --- Total display (NEW) ---
        summaryPanel.add(Box.createVerticalStrut(12));

        totalTitleLabel = new JLabel("Total:");
        totalTitleLabel.setFont(UIManager.getFont("Label.font"));
        totalTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(totalTitleLabel);
        summaryPanel.add(Box.createVerticalStrut(6));

        totalAmountLabel = new JLabel("\u20B10.00"); // initial value
        try {
            totalAmountLabel.setFont(fontManager.h3()); // use panel font helper if present
        } catch (Exception ignored) {
            totalAmountLabel.setFont(UIManager.getFont("Label.font"));
        }
        totalAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(totalAmountLabel);

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
        JLabel label = new JLabel("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>" + 
            text.replace("\n", "<br>") + "</body></html>");
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
                ((CardLayout) parent.getLayout()).show(parent, "LAUNDROMATS");
            }
        });
        styleButton(goBackBtn);
        
        leftButtonPanel.add(goBackBtn);
        
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtonPanel.setOpaque(false);
        
        confirmBtn = new buttonCreator("Confirm Delivery", "Button.font", () -> {
            if (selectedServices.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please select at least one service.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String services = selectedServiceLabel.getText();
            String qty = quantitySummaryLabel.getText();
            String sep = separationSummaryLabel.getText();
            String instr = instructionsSummaryArea.getText();
            
            // Fetch address dynamically from database
            String userAddress = "Real Address in Davao City, Davao del Sur, Philippines";
            
            Container parent = getParent();
            if (parent instanceof JPanel) {
                JPanel parentPanel = (JPanel) parent;
                
                Component[] components = parentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof ConfirmPaymentPanel) {
                        parentPanel.remove(comp);
                        break;
                    }
                }
                
                // compute total from current selection and quantity (helper added below)
double computedTotal = getCurrentTotal();

// pass computedTotal into ConfirmPaymentPanel so display matches Pickup panel
parentPanel.add(new ConfirmPaymentPanel(services, qty, sep, instr, userAddress, computedTotal), "CONFIRM_PAYMENT");
((CardLayout) parentPanel.getLayout()).show(parentPanel, "CONFIRM_PAYMENT");
            }
        });
        styleButton(confirmBtn);
        
        rightButtonPanel.add(confirmBtn);
        
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);
        
        return bottomPanel;
    }

    // Compute current total from selected service buttons and the quantity field.
// Keeps exactly the same logic used in updateOrderSummary().
private double getCurrentTotal() {
    int qty = getQuantityValue(); // existing helper you added
    int effectiveQty = Math.max(1, qty);
    double total = 0.0;
    for (ServiceButton btn : serviceButtons) {
        if (btn != null && btn.isSelected()) {
            total += btn.getUnitPrice() * effectiveQty;
        }
    }
    return total;
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
    
    private void updateOrderSummary() {
        if (!isInitialized) return;

        // ----- Services list -----
        if (selectedServices.isEmpty()) {
            selectedServiceLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Availed Service/s:<br>None</body></html>");
        } else {
            StringBuilder sb = new StringBuilder("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Availed Service/s:<br>");
            for (String s : selectedServices) {
                sb.append("• ").append(s).append("<br>");
            }
            sb.append("</body></html>");
            selectedServiceLabel.setText(sb.toString());
        }

        // ----- Quantity -----
        int qty = getQuantityValue();
        quantitySummaryLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Quantity:<br>" + qty + "</body></html>");

        // ----- Separation -----
        String separationText = "Do Not Separate";
        if (yesBtn != null && yesBtn.isSelected()) separationText = "Separate";
        else if (noBtn != null && noBtn.isSelected()) separationText = "Do Not Separate";
        separationSummaryLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Separation:<br>" + separationText + "</body></html>");

        // ----- Instructions -----
        instructionsSummaryArea.setText(instructionsArea.getText().trim().isEmpty() ? "None" : instructionsArea.getText());

        // ----- Compute total from selected service buttons -----
        double total = 0.0;
        int effectiveQty = Math.max(1, qty);
        for (ServiceButton btn : serviceButtons) {
            if (btn != null && btn.isSelected()) {
                total += btn.getUnitPrice() * effectiveQty;
            }
        }

        // Update the total label
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.format("\u20B1%.2f", total));
        }
    }

        // Read the quantity field, return integer >=1 (default 1)
    private int getQuantityValue() {
        if (quantityField == null) return 1;
        String t = quantityField.getText().trim();
        if (t.isEmpty()) return 1;
        try {
            int v = Integer.parseInt(t.replaceAll("[^0-9]", ""));
            return Math.max(1, v);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
    
    private String getSelectedButtonText(ButtonGroup group) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.isSelected()) return b.getText();
        }
        return "";
    }
    
    private void updateThemeColors() {
        if (!isInitialized) return;
        
        boolean isDark = FlatLaf.isLafDark();
        Color foregroundColor = getAdaptiveForegroundColor();
        
        // Update title labels
        if (serviceTitleLabel != null) {
            serviceTitleLabel.setFont(fontManager.h2());
            serviceTitleLabel.setForeground(foregroundColor);
        }
        if (detailsTitleLabel != null) {
            detailsTitleLabel.setFont(fontManager.h2());
            detailsTitleLabel.setForeground(foregroundColor);
        }
        if (summaryTitleLabel != null) {
            summaryTitleLabel.setFont(fontManager.h2());
            summaryTitleLabel.setForeground(foregroundColor);
        }
        
        // Update all labels in details section
        if (separationLabel != null) separationLabel.setForeground(foregroundColor);
        if (instructionsTextLabel != null) instructionsTextLabel.setForeground(foregroundColor);
        
        // Update radio buttons
        if (kiloBtn != null) kiloBtn.setForeground(foregroundColor);
        if (articleBtn != null) articleBtn.setForeground(foregroundColor);
        if (yesBtn != null) yesBtn.setForeground(foregroundColor);
        if (noBtn != null) noBtn.setForeground(foregroundColor);
        
        // Update summary labels
        if (selectedServiceLabel != null) selectedServiceLabel.setForeground(foregroundColor);
        if (quantitySummaryLabel != null) quantitySummaryLabel.setForeground(foregroundColor);
        if (separationSummaryLabel != null) separationSummaryLabel.setForeground(foregroundColor);
        if (instructionsHeaderLabel != null) instructionsHeaderLabel.setForeground(foregroundColor);
        
        // Update text fields and text areas
        Color textFieldBackground = UIManager.getColor("TextField.background");
        Color textFieldForeground = UIManager.getColor("TextField.foreground");
        Color borderColor = UIManager.getColor("Component.borderColor");
        if (borderColor == null) {
            borderColor = isDark ? new Color(70, 70, 70) : new Color(200, 200, 200);
        }
        
        if (quantityField != null) {
            quantityField.setBackground(textFieldBackground);
            quantityField.setForeground(textFieldForeground);
            quantityField.setCaretColor(textFieldForeground);
        }
        
        if (instructionsArea != null) {
            instructionsArea.setBackground(textFieldBackground);
            instructionsArea.setForeground(textFieldForeground);
            instructionsArea.setCaretColor(textFieldForeground);
            instructionsArea.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(10, borderColor, 1),
                new EmptyBorder(8, 8, 8, 8)
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
        
        // Update service buttons
        for (ServiceButton btn : serviceButtons) {
            btn.updateThemeColors();
        }
        
        updateSectionBorders();
        
        repaint();
    }
    
    private void updateSectionBorders() {
        Color accent = getAccentBorderColor();
        updatePanelBorder(servicesSection, accent);
        updatePanelBorder(detailsSection, accent);
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
    
    private class ServiceButton extends roundedPanel {
    private boolean selected;
    private String text;
    private String iconPath;
    private double price;
    private JLabel iconLabel;
    private JLabel textLabel;
    private JLabel priceLabel;

    public ServiceButton(String text, String iconPath, double price) {
        super(16);
        this.text = text;
        this.iconPath = iconPath;
        this.price = price;

        setLayout(new BorderLayout(5, 5));
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, getAccentBorderColor(), 2),
            new EmptyBorder(15, 10, 15, 10)
        ));

        Icon icon = iconCreator.getIcon(iconPath, 40, 40);
        iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(iconLabel, BorderLayout.CENTER);

        // name label
        textLabel = new JLabel(text);
        textLabel.setFont(UIManager.getFont("Label.font"));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // price label (small, below the name)
        priceLabel = new JLabel(String.format("\u20B1%.2f / load", price));
        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont != null) {
            priceLabel.setFont(baseFont.deriveFont(Font.PLAIN, Math.max(10f, baseFont.getSize2D() - 1f)));
        }
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // stack name + price vertically
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(textLabel);
        south.add(Box.createVerticalStrut(4));
        south.add(priceLabel);
        add(south, BorderLayout.SOUTH);

        updateThemeColors();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
    selected = !selected;
    if (selected) {
        selectedServices.add(text); // Set prevents duplicates
        setBackground(new Color(100,150,255));
        setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, new Color(50, 100, 200), 2),
            new EmptyBorder(15, 10, 15, 10)
        ));
    } else {
        selectedServices.remove(text);
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, getAccentBorderColor(), 2),
            new EmptyBorder(15, 10, 15, 10)
        ));
    }
    updateOrderSummary();
}
            final Color limeHover = UIManager.getColor("Sidebar.hoverBackground") != null
                ? UIManager.getColor("Sidebar.hoverBackground")
                : new Color(0xDAEC73);
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!selected) {
                    setBackground(limeHover);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!selected) {
                    setOpaque(false);
                    setBackground(new Color(0,0,0,0));
                }
            }
        });
        
    }
        
        public void updateThemeColors() {
        Color textColor = getAdaptiveForegroundColor();
        if (textLabel != null) {
            textLabel.setForeground(textColor);
        }
        if (priceLabel != null) {
            // price label uses slightly muted color in light mode; in dark mode use white
            Color priceColor = isDarkTheme() ? Color.WHITE : new Color(0x4A4A4A);
            priceLabel.setForeground(priceColor);
        }
        if (!selected) {
            setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(16, getAccentBorderColor(), 2),
                new EmptyBorder(15, 10, 15, 10)
            ));
        }
    }
    
    public double getUnitPrice() { return price; }

    public boolean isSelected() {
            return selected;
        }
    
    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(FIXED_BACKGROUND);
        
        if (isInitialized) {
            layoutSections();
            updateThemeColors();
            if (goBackBtn != null) goBackBtn.rescaleNow();
            if (confirmBtn != null) confirmBtn.rescaleNow();
        }
    }
}           
}