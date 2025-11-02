package org.example.gui.utils.editprofile;

import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;

public class addressCard extends JPanel {
    public addressCard(Mainframe frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setMaximumSize(new Dimension(850, 500)); // container max size

        // form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIManager.getColor("Profile.background"));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(850, 370));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title row (span two columns)
        JLabel title = new JLabel("Shipping Address");
        fontManager.applyHeading(title, 9);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(title, gbc);

        // separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(sep, gbc);

        // reset for fields
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int labelWidth = 140;
        int txtFieldWidth = 180;
        int txtFieldHeight = 40;

        // Shipping Address 1
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblAddress1 = new JLabel("Shipping Address 1:");
        fontManager.applyHeading(lblAddress1, 8);
        lblAddress1.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblAddress1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField tfAddress1 = new JTextField("123 Main St");
        tfAddress1.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfAddress1.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        formPanel.add(tfAddress1, gbc);

        // Shipping Address 2
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblAddress2 = new JLabel("Shipping Address 2:");
        fontManager.applyHeading(lblAddress2, 8);
        lblAddress2.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblAddress2, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField tfAddress2 = new JTextField("Unit 4, Building X");
        tfAddress2.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfAddress2.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        formPanel.add(tfAddress2, gbc);

        // push button row down a bit
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalStrut(8), gbc);

        // save button aligned to right
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        JButton saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(120, 35));
        btnWrap.add(saveBtn);
        formPanel.add(btnWrap, gbc);

        add(formPanel);
    }
}
