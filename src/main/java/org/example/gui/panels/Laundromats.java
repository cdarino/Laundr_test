package org.example.gui.panels;

import org.example.gui.laundromats.LaundromatData;
import org.example.gui.laundromats.LaundromatListPanel;
import org.example.gui.laundromats.LaundromatDetailsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Laundromats panel – left side list, right side details with an inner CardLayout
 * that switches between details and pickup flow.
 */
public class Laundromats extends JPanel {

    private LaundromatListPanel listPanel;

    // Right side CardLayout (DETAILS | PICKUP)
    private JPanel rightCardPanel;
    private CardLayout rightCardLayout;
    private LaundromatDetailsPanel detailsPanel;
    private PickupPanel pickupPanel;

    public Laundromats() {
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left: list of laundromats; selecting one updates the details view
        listPanel = new LaundromatListPanel(this::showDetails);

        // Right: card container
        rightCardLayout = new CardLayout();
        rightCardPanel = new JPanel(rightCardLayout);

        // Create cards
        detailsPanel = new LaundromatDetailsPanel(this::showPickup);  // callback for Request Pickup
        pickupPanel = new PickupPanel(this::showDetailsPanel);        // callback for Go Back

        rightCardPanel.add(detailsPanel, "DETAILS");
        rightCardPanel.add(pickupPanel, "PICKUP");

        // Sizing
        Dimension listSize = new Dimension(305, 441);
        Dimension rightSize = new Dimension(425, 441);
        listPanel.setPreferredSize(listSize);
        rightCardPanel.setPreferredSize(rightSize);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        // Left column
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        add(listPanel, gbc);

        // Spacer
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(Box.createHorizontalStrut(12), gbc);

        // Right column (card panel)
        gbc.gridx++;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(rightCardPanel, gbc);

        // Start on DETAILS
        showDetailsPanel();
    }

    // Called when a list item is selected
    private void showDetails(LaundromatData data) {
        detailsPanel.setLaundromat(data);
        showDetailsPanel();
    }

    private void showPickup() {
        rightCardLayout.show(rightCardPanel, "PICKUP");
        rightCardPanel.revalidate();
        rightCardPanel.repaint();
    }

    private void showDetailsPanel() {
        rightCardLayout.show(rightCardPanel, "DETAILS");
        rightCardPanel.revalidate();
        rightCardPanel.repaint();
    }
}