package org.example.gui.utils.orders;

import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;
// new import
import java.text.DecimalFormat;

public class toReceiveCard extends JPanel {

    private JLabel orderInfo, laundromatLabel, services, addressLabel, paymentLabel, dateLabel;

    // formatter for currency
    private static final DecimalFormat df = new DecimalFormat("₱#,##0.00");

    public toReceiveCard(String orderId, String laundromat,
                         String address, String price, String eta) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(UIManager.getColor("background"));
        setBorder(BorderFactory.createCompoundBorder(
                // Use the original light gray border color/style
                BorderFactory.createLineBorder(new Color(0xE5E7EB), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        // Use Integer.MAX_VALUE to allow it to fill the width of the viewport
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Left Panel ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT); // Align content top

        orderInfo = new JLabel("Order " + orderId);
        laundromatLabel = new JLabel(laundromat);
        services = new JLabel("<html><ul style='margin:0;padding:0;list-style-type:none;'>" +
                "<li>Ongoing</li>" + // status for an ongoing card
                "</ul></html>");

        fontManager.applyHeading(orderInfo, 7);
        fontManager.applyHeading(laundromatLabel, 7);
        leftPanel.add(orderInfo);
        leftPanel.add(laundromatLabel);
        leftPanel.add(services);

        // --- Right Panel ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // --- ui kept identical ---
        addressLabel = new JLabel("<html>" + address + "</html>");
        paymentLabel = new JLabel("<html>" + price + "</html>");
        dateLabel = new JLabel("<html>" + eta + "</html>"); // use orderdate
        // ---

        fontManager.applyHeading(addressLabel, 8);
        fontManager.applyHeading(paymentLabel, 8);
        fontManager.applyHeading(dateLabel, 8);

        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(addressLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(paymentLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(dateLabel);

        add(leftPanel);
        add(Box.createHorizontalGlue()); // Pushes the right panel to the edge
        add(rightPanel);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        SwingUtilities.invokeLater(() -> {
            if (orderInfo != null) {
                fontManager.applyHeading(orderInfo, 7);
                fontManager.applyHeading(laundromatLabel, 7);
                fontManager.applyHeading(services, 8);
                fontManager.applyHeading(addressLabel, 8);
                fontManager.applyHeading(paymentLabel, 8);
                fontManager.applyHeading(dateLabel, 8);
            }
        });
    }
}