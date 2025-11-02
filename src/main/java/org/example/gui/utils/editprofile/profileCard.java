package org.example.gui.utils.editprofile;

import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;

public class profileCard extends JPanel {
    public profileCard(Mainframe frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setMaximumSize(new Dimension(850, 500)); // container max size

        // profile panel
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
        JLabel profileTitle = new JLabel("My Profile");
        fontManager.applyHeading(profileTitle, 9);
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(profileTitle, gbc);

        // jseparator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(sep, gbc);

        // reset for fields: two columns
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int labelWidth = 140;
        int txtFieldWidth = 180;
        int txtFieldHeight = 40;

        // username
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblUser = new JLabel("Username:");
        fontManager.applyHeading(lblUser, 8);
        lblUser.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField tfUser = new JTextField(frame != null && frame.getCurrentUser() != null ? frame.getCurrentUser() : "");
        tfUser.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfUser.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfUser.setBackground(UIManager.getColor("TextField.background"));
        formPanel.add(tfUser, gbc);

        // emailw
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblEmail = new JLabel("Email:");
        fontManager.applyHeading(lblEmail, 8);
        lblEmail.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField tfEmail = new JTextField("johndoe@gmail.com");
        tfEmail.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfEmail.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfEmail.setBackground(UIManager.getColor("TextField.background"));
        formPanel.add(tfEmail, gbc);

        // phone num
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblPhone = new JLabel("Phone Number:");
        fontManager.applyHeading(lblPhone, 8);
        lblPhone.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblPhone, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField tfPhone = new JTextField("09233215644");
        tfPhone.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfPhone.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfPhone.setBackground(UIManager.getColor("TextField.background"));
        formPanel.add(tfPhone, gbc);

        // pushes save button down a bit
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalStrut(8), gbc);

        // save button row aligned to right
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        JButton save = new JButton("Save");
        save.setPreferredSize(new Dimension(100, 35));
        btnWrap.add(save);
        formPanel.add(btnWrap, gbc);

        add(formPanel);
    }
}
