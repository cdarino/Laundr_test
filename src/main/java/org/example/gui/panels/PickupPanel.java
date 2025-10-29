package org.example.gui.panels;

import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.iconCreator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PickupPanel extends JPanel {
    private JPanel servicesPanel, detailsPanel, summaryPanel;
    private JTextField quantityField;
    private ButtonGroup serviceTypeGroup, colorSeparationGroup;
    private JTextArea instructionsArea;
    private List<String> selectedServices = new ArrayList<>();
    private JLabel selectedServiceLabel, quantityLabel, separationLabel, instructionsLabel;

    public PickupPanel() {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        String[] titles = {"Select Services", "Details", "Order Summary"};
        JPanel[] panels = {createServicesPanel(), createDetailsPanel(), createSummaryPanel()};

        for (int i = 0; i < 3; i++) {
            roundedPanel section = new roundedPanel(20);
            section.setLayout(new BorderLayout());
            section.setBackground(UIManager.getColor("Sidebarbtn.background"));
            section.add(createSectionTitle(titles[i]), BorderLayout.NORTH);
            section.add(panels[i], BorderLayout.CENTER);

            gbc.gridx = i;
            gbc.weightx = 0.33;
            gbc.insets = new Insets(0, i == 0 ? 0 : 10, 0, i == 2 ? 0 : 10);
            mainContainer.add(section, gbc);
        }

        add(mainContainer, BorderLayout.CENTER);
        add(createBottomButtons(), BorderLayout.SOUTH);
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(new EmptyBorder(0, 20, 0, 0));
        label.setForeground(Color.BLACK);
        label.setFont(UIManager.getFont("Title.font"));
        return label;
    }

    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[][] services = {
                {"Dry Clean", "Icons/dryClean.svg"},
                {"Laundry", "Icons/washandFold.svg"},
                {"Iron", "Icons/iron.svg"}
        };

        for (String[] svc : services) panel.add(new ServiceButton(svc[0], svc[1]));
        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // --- Service Type ---
        serviceTypeGroup = new ButtonGroup();
        JRadioButton kiloBtn = new JRadioButton("Kilograms");
        JRadioButton articleBtn = new JRadioButton("Articles of Clothing");
        for (JRadioButton b : new JRadioButton[]{kiloBtn, articleBtn}) {
            b.setForeground(Color.BLACK);
            serviceTypeGroup.add(b);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(kiloBtn, gbc);
        gbc.gridx = 1;
        panel.add(articleBtn, gbc);

        // --- Quantity ---
        quantityField = new JTextField(7);
        quantityField.setPreferredSize(new Dimension(80, 28));
        quantityField.setVisible(false);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(quantityField);
        panel.add(qtyPanel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Separation ---
        JLabel sepLabel = new JLabel("Separate Colored & Non-Colored?");
        sepLabel.setForeground(Color.BLACK);
        gbc.gridy = 2;
        panel.add(sepLabel, gbc);

        colorSeparationGroup = new ButtonGroup();
        JRadioButton yesBtn = new JRadioButton("Yes"), noBtn = new JRadioButton("No");
        for (JRadioButton b : new JRadioButton[]{yesBtn, noBtn}) {
            b.setForeground(Color.BLACK);
            colorSeparationGroup.add(b);
        }

        gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.gridx = 0; panel.add(yesBtn, gbc);
        gbc.gridx = 1; panel.add(noBtn, gbc);

        // --- Instructions ---
        JLabel instrLabel = new JLabel("Custom Instructions");
        instrLabel.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(instrLabel, gbc);

        instructionsArea = new JTextArea(5, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        gbc.gridy = 5; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        panel.add(instructionsArea, gbc);

        // --- Logic ---
        ActionListener refresh = e -> { quantityField.setVisible(true); updateOrderSummary(); };
        kiloBtn.addActionListener(refresh);
        articleBtn.addActionListener(refresh);

        ActionListener updateSummary = e -> updateOrderSummary();
        yesBtn.addActionListener(updateSummary);
        noBtn.addActionListener(updateSummary);

        javax.swing.event.DocumentListener docListener = new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateOrderSummary(); }
        };
        quantityField.getDocument().addDocumentListener(docListener);
        instructionsArea.getDocument().addDocumentListener(docListener);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        selectedServiceLabel = createLabel("Availed Service/s: None");
        quantityLabel = createLabel("0");
        separationLabel = createLabel("Do Not Separate Colored & Non-Colored");
        instructionsLabel = createLabel("No Custom Instructions");

        int space = 30;
        for (JLabel lbl : new JLabel[]{selectedServiceLabel, quantityLabel, separationLabel, instructionsLabel}) {
            panel.add(lbl);
            panel.add(Box.createVerticalStrut(space));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel("<html><body style='width:220px;'>" + text + "</body></html>");
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createBottomButtons() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        buttonCreator goBack = new buttonCreator("Go Back", "Button.font", () -> {
            Container parent = getParent();
            if (parent instanceof JPanel) {
                ((CardLayout) parent.getLayout()).show(parent, "LAUNDROMATS");
            }
        });

        buttonCreator confirm = new buttonCreator("Confirm Delivery", "Button.font", () ->
                JOptionPane.showMessageDialog(this, "Pickup scheduled successfully!")
        );

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(goBack);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(confirm);

        bottomPanel.add(left, BorderLayout.WEST);
        bottomPanel.add(right, BorderLayout.EAST);
        return bottomPanel;
    }

    private void updateOrderSummary() {
        // Services
        if (selectedServices.isEmpty()) selectedServiceLabel.setText("<html><body style='width:220px;'>Availed Service/s: None</body></html>");
        else {
            StringBuilder sb = new StringBuilder("<html><body style='width:220px;'>Availed Service/s:<br>");
            selectedServices.forEach(s -> sb.append("- ").append(s).append("<br>"));
            sb.append("</body></html>");
            selectedServiceLabel.setText(sb.toString());
        }

        // Quantity
        String quantityText = quantityField.getText().trim();
        quantityLabel.setText("<html><body style='width:220px;'>" +
                (quantityText.isEmpty() ? "0" :
                        quantityText + " " + (getSelectedButtonText(serviceTypeGroup).equals("Kilograms") ?
                                "Kilograms" : "Articles of Clothing")) + "</body></html>");

        // Separation
        String sepText = getSelectedButtonText(colorSeparationGroup);
        separationLabel.setText("<html><body style='width:220px;'>" +
                (sepText.equals("Yes") ? "Separate Colored & Non-Colored"
                        : "Do Not Separate Colored & Non-Colored") + "</body></html>");

        // Instructions
        String note = instructionsArea.getText().trim();
        instructionsLabel.setText("<html><body style='width:220px;'>" +
                (note.isEmpty() ? "No Custom Instructions" : note) + "</body></html>");
    }

    private String getSelectedButtonText(ButtonGroup group) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b.isSelected()) return b.getText();
        }
        return "";
    }

    private class ServiceButton extends roundedPanel {
        private boolean selected;
        
        public ServiceButton(String text, String iconPath) {
            super(20);
            setPreferredSize(new Dimension(100, 120));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(UIManager.getColor("Sidebarbtn.background"));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            JLabel icon = new JLabel(iconCreator.getIcon(iconPath, 48, 48));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel label = new JLabel("<html><center>" + text + "</center.</html>");
            label.setForeground(Color.BLACK);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            add(Box.createVerticalStrut(10));
            add(icon);
            add(Box.createVerticalStrut(10));
            add(label);
            add(Box.createVerticalGlue());
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selected = !selected;
                    if (selected) selectedServices.add(text);
                    else selectedServices.remove(text);
                    setBackground(UIManager.getColor(selected ? "Button.pressedBackground" : " Sidebarbtn.background"));
                    updateOrderSummary();
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!selected) setBackground(UIManager.getColor("Button.hoverBackground"));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!selected) setBackground(UIManager.getColor("Sidebarbtn.background"));
                }
            });
        }
    }                
    
    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.Background"));
        if (servicesPanel != null) servicesPanel.setBackground(UIManager.getColor("Panel.background"));
        if (detailsPanel != null) detailsPanel.setBackground(UIManager.getColor("Sidebarbtn.background"));
        if (summaryPanel != null) summaryPanel.setBackground(UIManager.getColor("Panel.background"));
    }
}
