package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;

import javax.swing.*;
import java.awt.*;

public class Register extends JPanel {
    private final Mainframe frame;

    private JLabel title, userLabel, emailLabel, phoneLabel, passLabel, confirmLabel;
    private JTextField userField, emailField, phoneField;
    private JPasswordField passField, confirmField;
    private buttonCreator registerBtn;
    private roundedPanel card;

    public Register(Mainframe frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("background"));

        card = new roundedPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(UIManager.getColor("Register.background"));
        card.setBorder(new roundedBorder(30, UIManager.getColor("listBorder"), 0));
        card.setPreferredSize(new Dimension(450, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;

        title = new JLabel("Register", SwingConstants.CENTER);
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 30, 0);
        card.add(title, gbc);

        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        userLabel = new JLabel("Username");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(userLabel, gbc);

        userField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(userField, gbc);

        emailLabel = new JLabel("Email Address");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(emailField, gbc);

        phoneLabel = new JLabel("Phone Number");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(phoneField, gbc);

        passLabel = new JLabel("Password");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(passLabel, gbc);

        passField = new JPasswordField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(passField, gbc);

        confirmLabel = new JLabel("Confirm Password");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(confirmLabel, gbc);

        confirmField = new JPasswordField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 25, 20); // extra space before button
        card.add(confirmField, gbc);

        registerBtn = new buttonCreator("Register", "Button.font", () -> frame.showCard("LOGIN"));
        registerBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 20, 0);
        card.add(registerBtn, gbc);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.anchor = GridBagConstraints.CENTER;
        add(card, outer);

        applyStyles();
    }

    private void applyStyles() {
        setBackground(UIManager.getColor("Register.background") != null
                ? UIManager.getColor("Register.background")
                : new Color(52, 152, 219));

        card.setBackground(UIManager.getColor("Register.panel"));

        title.setFont(UIManager.getFont("Title.font"));
        title.setForeground(UIManager.getColor("headerColor"));

        JLabel[] labels = {userLabel, emailLabel, phoneLabel, passLabel, confirmLabel};
        for (JLabel label : labels) {
            label.setFont(UIManager.getFont("Button.font"));
            label.setForeground(UIManager.getColor("Label.foreground"));
        }

        JTextField[] fields = {userField, emailField, phoneField};
        for (JTextField field : fields) {
            field.setFont(UIManager.getFont("defaultFont"));
            field.setBorder(BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1));
        }

        JPasswordField[] passFields = {passField, confirmField};
        for (JPasswordField pf : passFields) {
            pf.setFont(UIManager.getFont("defaultFont"));
            pf.setBorder(BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1));
        }

        registerBtn.setBackground(UIManager.getColor("Button.background"));
        registerBtn.setForeground(UIManager.getColor("Button.foreground"));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (card != null) {
            SwingUtilities.invokeLater(this::applyStyles);
        }
    }
}
