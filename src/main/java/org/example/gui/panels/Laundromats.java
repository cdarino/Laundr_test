package org.example.gui.panels;

import org.example.gui.laundromats.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Laundromats panel – left side list, right side details.
 */
public class Laundromats extends JPanel {

    private LaundromatListPanel listPanel;
    private LaundromatDetailsPanel detailsPanel;

    public Laundromats() {
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        listPanel = new LaundromatListPanel(this::showDetails);
        detailsPanel = new LaundromatDetailsPanel();

        // --- sizing ---
        Dimension listSize = new Dimension(305, 441);   // slightly narrower
        Dimension detailsSize = new Dimension(425, 441);
        listPanel.setPreferredSize(listSize);
        detailsPanel.setPreferredSize(detailsSize);

        // --- layout setup ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0; // vertical fill

        // Left panel (Laundromat list)
        gbc.gridx = 0;
        gbc.weightx = 0.42; // proportional to 305 / (305+425)
        add(listPanel, gbc);

        // Add spacing between panels
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 12, 0, 12);
        add(Box.createHorizontalStrut(12), gbc);

        // Right panel (Details)
        gbc.gridx++;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(detailsPanel, gbc);
    }

    private void showDetails(LaundromatData data) {
        detailsPanel.setLaundromat(data);
    }
}
