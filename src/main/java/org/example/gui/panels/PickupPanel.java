package org.example.gui.panels;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.fonts.fontLoader;
import org.example.gui.utils.fonts.fontManager;
import com.formdev.flatlaf.FlatLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
public class PickupPanel extends JPanel {
    // Fixed background color (never changes)
    private static final Color FIXED_BACKGROUND = new Color(245, 245, 245);
    
    // Section proportions (25% / 55% / 20%)
    private static final double SERVICES_RATIO = 0.25;
    private static final double DETAILS_RATIO = 0.55;
    private static final double SUMMARY_RATIO = 0.20;
    
    // Spacing constants
    private static final int SECTION_GAP = 20;
    private static final int PADDING = 20;
    
    private JPanel mainContainer;
    private roundedPanel servicesSection, detailsSection, summarySection;
    private JPanel servicesPanel, detailsPanel, summaryPanel;
    
    // Services section components
    private JLabel serviceTitleLabel;
    private List<ServiceButton> serviceButtons = new ArrayList<>();
    private List<String> selectedServices = new ArrayList<>();
    
    // Details section components
    private JLabel detailsTitleLabel;
    private JRadioButton kiloBtn, articleBtn, yesBtn, noBtn;
    private ButtonGroup serviceTypeGroup, colorSeparationGroup;
    private JTextField quantityField;
    private JTextArea instructionsArea;
    private JLabel separationLabel, instructionsTextLabel;
    
    // Summary section components
    private JLabel summaryTitleLabel;
    private JLabel selectedServiceLabel, quantitySummaryLabel, separationSummaryLabel;
    private JTextArea instructionsSummaryArea;
    
    // Bottom buttons
    private buttonCreator goBackBtn, confirmBtn;
    
    private boolean isInitialized = false;
    public PickupPanel() {
        // Load fonts first
        fontLoader.loadFonts();
        
        setLayout(new BorderLayout());
        setBackground(FIXED_BACKGROUND);
        
        // Main container with padding
        mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Create the three sections
        initializeSections();
        
        // Create bottom buttons
        JPanel bottomPanel = createBottomButtons();
        
        add(mainContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Mark as initialized
        isInitialized = true;
        
        // Listen for theme changes
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName())) {
                    SwingUtilities.invokeLater(() -> updateThemeColors());
                }
            }
        });
        
        // Listen for resize events to maintain proportions
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutSections();
            }
        });
        
        // Initial theme color update
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
        
        // Services section (25%)
        gbc.gridx = 0;
        gbc.weightx = SERVICES_RATIO;
        gbc.weighty = 1.0;
        mainContainer.add(servicesSection, gbc);
        
        // Details section (55%)
        gbc.gridx = 1;
        gbc.weightx = DETAILS_RATIO;
        mainContainer.add(detailsSection, gbc);
        
        // Summary section (20%)
        gbc.gridx = 2;
        gbc.weightx = SUMMARY_RATIO;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(summarySection, gbc);
        
        mainContainer.revalidate();
        mainContainer.repaint();
    }
    
    private roundedPanel createServicesSection() {
        roundedPanel section = new roundedPanel(16);
        section.setLayout(new BorderLayout(10, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        serviceTitleLabel = new JLabel("Select Services");
        serviceTitleLabel.setFont(fontManager.h2());
        serviceTitleLabel.setForeground(Color.BLACK); // Always black
        section.add(serviceTitleLabel, BorderLayout.NORTH);
        
        // Services panel with grid layout
        servicesPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        servicesPanel.setOpaque(false);
        
        String[][] services = {
            {"Dry Clean", "Icons/Services/dryClean.svg"},
            {"Laundry", "Icons/Services/washandFold.svg"},
            {"Iron", "Icons/Services/iron.svg"}
        };
        
        for (String[] svc : services) {
            ServiceButton btn = new ServiceButton(svc[0], svc[1]);
            servicesPanel.add(btn);
            serviceButtons.add(btn);
        }
        
        JScrollPane scrollPane = new JScrollPane(servicesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(scrollPane, BorderLayout.CENTER);
        
        return section;
    }
    
    private roundedPanel createDetailsSection() {
        roundedPanel section = new roundedPanel(16);
        section.setLayout(new BorderLayout(10, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        detailsTitleLabel = new JLabel("Details");
        detailsTitleLabel.setFont(fontManager.h2());
        detailsTitleLabel.setForeground(Color.BLACK); // Always black
        section.add(detailsTitleLabel, BorderLayout.NORTH);
        
        // Details panel with improved spacing
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        // Section 1: Service Type Radio Buttons (Kilograms | Articles) in one row
        JPanel serviceTypeRadioPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        serviceTypeRadioPanel.setOpaque(false);
        serviceTypeRadioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        serviceTypeRadioPanel.setMaximumSize(new Dimension(450, 30));
        
        serviceTypeGroup = new ButtonGroup();
        kiloBtn = new JRadioButton("Kilograms");
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
        
        // Shared quantity text field (directly below both radio columns)
        JPanel quantityFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityFieldPanel.setOpaque(false);
        quantityFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        quantityField = new JTextField(15);
        quantityField.setFont(UIManager.getFont("TextField.font"));
        quantityField.setPreferredSize(new Dimension(250, 32));
        quantityFieldPanel.add(quantityField);
        
        detailsPanel.add(quantityFieldPanel);
        detailsPanel.add(Box.createVerticalStrut(20));
        
        // Section 2: Separation Label + Yes/No Radio Buttons
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
        
        // Section 3: Custom Instructions Label + Text Area
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
        detailsScroll.setOpaque(false);
        detailsScroll.getViewport().setOpaque(false);
        detailsScroll.setBorder(null);
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(detailsScroll, BorderLayout.CENTER);
        
        // Add listeners
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
        
        return section;
    }
    
    private roundedPanel createSummarySection() {
        roundedPanel section = new roundedPanel(16);
        section.setLayout(new BorderLayout(10, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new roundedBorder(16, new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        summaryTitleLabel = new JLabel("Order Summary");
        summaryTitleLabel.setFont(fontManager.h2());
        summaryTitleLabel.setForeground(Color.BLACK); // Always black
        section.add(summaryTitleLabel, BorderLayout.NORTH);
        
        // Summary panel
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);
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
        
        // Instructions Summary - using JTextArea to match Details section styling
        JLabel instructionsHeaderLabel = new JLabel("Instructions:");
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
        // Use rounded border like Details section
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
        
        // Wrap in scroll pane to handle overflow - vertical only
        JScrollPane summaryScroll = new JScrollPane(summaryPanel);
        summaryScroll.setOpaque(false);
        summaryScroll.getViewport().setOpaque(false);
        summaryScroll.setBorder(null);
        summaryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        summaryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        section.add(summaryScroll, BorderLayout.CENTER);
        
        return section;
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
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, PADDING, PADDING, PADDING));
        
        // Left panel for Go Back button
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtonPanel.setOpaque(false);
        
        goBackBtn = new buttonCreator("Go Back", "Button.font", () -> {
            Container parent = getParent();
            if (parent instanceof JPanel) {
                ((CardLayout) parent.getLayout()).show(parent, "LAUNDROMATS");
            }
        });
        
        leftButtonPanel.add(goBackBtn);
        
        // Right panel for Confirm button
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtonPanel.setOpaque(false);
        
        confirmBtn = new buttonCreator("Confirm Delivery", "Button.font", () -> {
            // Validate that at least one service is selected
            if (selectedServices.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please select at least one service.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get all order summary data (as HTML)
            String services = selectedServiceLabel.getText();
            String qty = quantitySummaryLabel.getText();
            String sep = separationSummaryLabel.getText();
            String instr = instructionsSummaryArea.getText();
            
            // TODO: Fetch user address from database using CustomerDAO
            // For now, using placeholder
            String userAddress = "123 Main Street, Davao City, Davao del Sur, Philippines";
            
            // Navigate to ConfirmPaymentPanel with order data
            Container parent = getParent();
            if (parent instanceof JPanel) {
                JPanel parentPanel = (JPanel) parent;
                
                // Remove existing CONFIRM_PAYMENT card if it exists
                Component[] components = parentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof ConfirmPaymentPanel) {
                        parentPanel.remove(comp);
                        break;
                    }
                }
                
                // Add new ConfirmPaymentPanel with current data
                parentPanel.add(new ConfirmPaymentPanel(services, qty, sep, instr, userAddress), "CONFIRM_PAYMENT");
                ((CardLayout) parentPanel.getLayout()).show(parentPanel, "CONFIRM_PAYMENT");
            }
        });
        
        rightButtonPanel.add(confirmBtn);
        
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    private void updateOrderSummary() {
        if (!isInitialized) return;
        
        // Services
        if (selectedServices.isEmpty()) {
            selectedServiceLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Availed Service/s:<br>None</body></html>");
        } else {
            StringBuilder sb = new StringBuilder("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Availed Service/s:<br>");
            selectedServices.forEach(s -> sb.append("• ").append(s).append("<br>"));
            sb.append("</body></html>");
            selectedServiceLabel.setText(sb.toString());
        }
        
        // Quantity
        String quantityText = quantityField.getText().trim();
        String serviceType = getSelectedButtonText(serviceTypeGroup);
        if (quantityText.isEmpty() || serviceType.isEmpty()) {
            quantitySummaryLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Quantity:<br>0</body></html>");
        } else {
            String unit = serviceType.equals("Kilograms") ? "kg" : "pcs";
            quantitySummaryLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Quantity:<br>" + 
                quantityText + " " + unit + "</body></html>");
        }
        
        // Separation
        String sepText = getSelectedButtonText(colorSeparationGroup);
        String sepDisplay = sepText.equals("Yes") ? "Separate" : "Do Not Separate";
        separationSummaryLabel.setText("<html><body style='width: 100%; word-wrap: break-word; overflow-wrap: break-word;'>Separation:<br>" + 
            sepDisplay + "</body></html>");
        
        // Instructions - using JTextArea to prevent horizontal overflow
        String instructions = instructionsArea.getText().trim();
        if (instructions.isEmpty()) {
            instructionsSummaryArea.setText("None");
        } else {
            // Truncate if too long (more than 250 characters)
            if (instructions.length() > 250) {
                instructionsSummaryArea.setText(instructions.substring(0, 247) + "...");
            } else {
                instructionsSummaryArea.setText(instructions);
            }
        }
        
        // Force revalidation to ensure proper layout
        summaryPanel.revalidate();
        summaryPanel.repaint();
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
        
        // Update title labels - ALWAYS BLACK
        if (serviceTitleLabel != null) {
            serviceTitleLabel.setFont(fontManager.h2());
            serviceTitleLabel.setForeground(Color.BLACK);
        }
        if (detailsTitleLabel != null) {
            detailsTitleLabel.setFont(fontManager.h2());
            detailsTitleLabel.setForeground(Color.BLACK);
        }
        if (summaryTitleLabel != null) {
            summaryTitleLabel.setFont(fontManager.h2());
            summaryTitleLabel.setForeground(Color.BLACK);
        }
        
        // Update non-heading labels - always black
        Color labelColor = Color.BLACK;
        if (separationLabel != null) separationLabel.setForeground(labelColor);
        if (instructionsTextLabel != null) instructionsTextLabel.setForeground(labelColor);
        
        // Update radio buttons
        if (kiloBtn != null) kiloBtn.setForeground(labelColor);
        if (articleBtn != null) articleBtn.setForeground(labelColor);
        if (yesBtn != null) yesBtn.setForeground(labelColor);
        if (noBtn != null) noBtn.setForeground(labelColor);
        
        // Update summary labels
        if (selectedServiceLabel != null) selectedServiceLabel.setForeground(labelColor);
        if (quantitySummaryLabel != null) quantitySummaryLabel.setForeground(labelColor);
        if (separationSummaryLabel != null) separationSummaryLabel.setForeground(labelColor);
        
        // Get text field colors from UIManager for consistency
        Color textFieldBackground = UIManager.getColor("TextField.background");
        Color textFieldForeground = UIManager.getColor("TextField.foreground");
        Color borderColor = UIManager.getColor("Component.borderColor");
        if (borderColor == null) {
            borderColor = isDark ? new Color(70, 70, 70) : new Color(200, 200, 200);
        }
        
        // Update text field in Details section
        if (quantityField != null) {
            quantityField.setBackground(textFieldBackground);
            quantityField.setForeground(textFieldForeground);
            quantityField.setCaretColor(textFieldForeground);
        }
        
        // Update text area in Details section
        if (instructionsArea != null) {
            instructionsArea.setBackground(textFieldBackground);
            instructionsArea.setForeground(textFieldForeground);
            instructionsArea.setCaretColor(textFieldForeground);
            instructionsArea.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(10, borderColor, 1),
                new EmptyBorder(8, 8, 8, 8)
            ));
        }
        
        // Update instructions summary area in Summary section to match Details section
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
        
        repaint();
    }
    
    private class ServiceButton extends roundedPanel {
        private boolean selected;
        private String text;
        private String iconPath;
        private JLabel iconLabel;
        private JLabel textLabel;
        
        public ServiceButton(String text, String iconPath) {
            super(16);
            this.text = text;
            this.iconPath = iconPath;
            
            setLayout(new BorderLayout(5, 5));
            setBackground(new Color(240, 240, 240));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(16, new Color(200, 200, 200), 1),
                new EmptyBorder(15, 10, 15, 10)
            ));
            
            // Load icon from resources
            Icon icon = iconCreator.getIcon(iconPath, 40, 40);
            iconLabel = new JLabel(icon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, BorderLayout.CENTER);
            
            // Text
            textLabel = new JLabel(text);
            textLabel.setFont(UIManager.getFont("Label.font"));
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(textLabel, BorderLayout.SOUTH);
            
            updateThemeColors();
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selected = !selected;
                    if (selected) {
                        selectedServices.add(text);
                        setBackground(new Color(100, 150, 255));
                        setBorder(BorderFactory.createCompoundBorder(
                            new roundedBorder(16, new Color(50, 100, 200), 2),
                            new EmptyBorder(15, 10, 15, 10)
                        ));
                    } else {
                        selectedServices.remove(text);
                        setBackground(new Color(240, 240, 240));
                        setBorder(BorderFactory.createCompoundBorder(
                            new roundedBorder(16, new Color(200, 200, 200), 1),
                            new EmptyBorder(15, 10, 15, 10)
                        ));
                    }
                    updateOrderSummary();
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!selected) {
                        setBackground(new Color(220, 220, 220));
                    }
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!selected) {
                        setBackground(new Color(240, 240, 240));
                    }
                }
            });
        }
        
        public void updateThemeColors() {
            Color textColor = Color.BLACK;
            if (textLabel != null) {
                textLabel.setForeground(textColor);
            }
        }
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        // Prevent background from changing - keep it fixed
        setBackground(FIXED_BACKGROUND);
        
        // Relayout sections to maintain proportions
        if (isInitialized) {
            layoutSections();
            updateThemeColors();
        }
    }
}
