package org.example.gui.utils.orders;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.states.OrderState;
import org.example.gui.utils.orders.states.OrderStateFactory;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class orderCard extends JPanel {

    private JLabel orderInfo, laundromatLabel, services, addressLabel, paymentLabel, dateLabel, stateLabel;
    private static final DecimalFormat df = new DecimalFormat("₱#,##0.00");

    public orderCard(String orderId, String laundromat,
                     String address, String price, String date, String orderStatus, String servicesList) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(UIManager.getColor("background"));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E7EB), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // === LEFT COLUMN ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        orderInfo = new JLabel("Order " + orderId);
        laundromatLabel = new JLabel(laundromat);
        String serviceText = (servicesList != null && !servicesList.isEmpty()) ? servicesList : "No services listed";
        services = new JLabel(serviceText);
        /* services = new JLabel("<html><ul style='margin:0;padding-left:20px;'>" +
                "<li>Wash & Fold</li>" +
                "<li>Dry Cleaning</li>" +
                "</ul></html>"); // placeholder
        */
        fontManager.applyHeading(orderInfo, 7);
        fontManager.applyHeading(laundromatLabel, 7);
        fontManager.applyHeading(services, 8);

        orderInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        laundromatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        services.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(orderInfo);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(laundromatLabel);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(services);

        // === MIDDLE COLUMN ===
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setOpaque(false);
        middlePanel.setAlignmentY(Component.TOP_ALIGNMENT);
        middlePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // padding

        addressLabel = new JLabel("<html>Delivered at: " + address + "</html>");
        paymentLabel = new JLabel("<html>" + price + "</html>");
        dateLabel = new JLabel("<html> Order Placed: " + date + "</html>");

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

        // === RIGHT COLUMN (STATE) ===
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        stateLabel = new JLabel(); // created empty
        stateLabel.setOpaque(true);
        stateLabel.setForeground(Color.WHITE); // text is always white
        stateLabel.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        stateLabel.setFont(stateLabel.getFont().deriveFont(Font.BOLD, 11f));

        // --- apply the state pattern ---
        OrderState state = OrderStateFactory.getState(orderStatus);
        stateLabel.setText(state.getText());
        stateLabel.setBackground(state.getColor());

        rightPanel.add(stateLabel);

        // === ADD TO MAIN CARD ===
        add(Box.createHorizontalStrut(10));
        add(leftPanel);
        add(Box.createHorizontalGlue()); // pushes the right panel to the edge
        add(Box.createHorizontalStrut(200));
        add(middlePanel);
        add(Box.createHorizontalGlue());
        add(rightPanel);
        add(Box.createHorizontalStrut(10));
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