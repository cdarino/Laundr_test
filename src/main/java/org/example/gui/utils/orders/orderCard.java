package org.example.gui.utils.orders;

import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;

public class orderCard extends JPanel {

    private JLabel orderInfo, laundromatLabel, services, addressLabel, paymentLabel, etaLabel;

    public orderCard(String orderId, String weight, String laundromat,
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

        orderInfo = new JLabel("Order " + orderId + " | " + weight);
        laundromatLabel = new JLabel(laundromat);
        services = new JLabel("<html><ul style='margin:0;padding-left:15;'>"
                + "<li>Wash and Fold</li>"
                + "<li>Dry Clean</li>"
                + "</ul></html>");

        fontManager.applyHeading(orderInfo,7);
        fontManager.applyHeading(laundromatLabel, 7);
        fontManager.applyHeading(services, 8);

        // align all labels to left
        orderInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        laundromatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        services.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(orderInfo);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(laundromatLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(services);

        // middle
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setOpaque(false);
        middlePanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60)); // adjust spacing between columns
        middlePanel.setAlignmentY(Component.TOP_ALIGNMENT);

        addressLabel = new JLabel("<html>" + address + "</html>");
        paymentLabel = new JLabel("<html>" + price + "</html>");
        etaLabel = new JLabel("<html>" + date + "</html>");

        fontManager.applyHeading(addressLabel, 8);
        fontManager.applyHeading(paymentLabel, 8);
        fontManager.applyHeading(etaLabel, 8);

        // align labels to the left
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        etaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        middlePanel.add(Box.createVerticalStrut(5));
        middlePanel.add(addressLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(paymentLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(etaLabel);

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
                fontManager.applyHeading(etaLabel, 8);
            }

            revalidate();
            repaint();
        });
    }
}
