package org.example.gui.utils.orders;

import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;
// new import
import java.text.DecimalFormat;

public class orderCard extends JPanel {

    private JLabel orderInfo, laundromatLabel, services, addressLabel, paymentLabel, dateLabel;

    // formatter for currency
    private static final DecimalFormat df = new DecimalFormat("₱#,##0.00");

    public orderCard(String orderId, String laundromat,
                     String address, String price, String date) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(UIManager.getColor("background"));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E7EB), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // left
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        orderInfo = new JLabel("Order " + orderId);
        laundromatLabel = new JLabel(laundromat);
        services = new JLabel("<html><ul style='margin:0;padding:0;list-style-type:none;'>" +
                "<li>Completed</li>" + // status for a completed card
                "</ul></html>");
        // ---

        fontManager.applyHeading(orderInfo, 7);
        fontManager.applyHeading(laundromatLabel, 7);
        leftPanel.add(orderInfo);
        leftPanel.add(laundromatLabel);
        leftPanel.add(services);

        // --- Middle Panel ---
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setOpaque(false);
        middlePanel.setAlignmentY(Component.TOP_ALIGNMENT); // align content top

        // --- ui kept identical ---
        addressLabel = new JLabel("<html>" + address + "</html>");
        paymentLabel = new JLabel("<html>" + price + "</html>");
        dateLabel = new JLabel("<html>" + date + "</html>"); // use orderdate
        // ---

        fontManager.applyHeading(addressLabel, 8);
        fontManager.applyHeading(paymentLabel, 8);
        fontManager.applyHeading(dateLabel, 8);

        // align labels to the left
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        middlePanel.add(Box.createVerticalStrut(5));
        middlePanel.add(addressLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(paymentLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(dateLabel);

        add(leftPanel);
        add(middlePanel);
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