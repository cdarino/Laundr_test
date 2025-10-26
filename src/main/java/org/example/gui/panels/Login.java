package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.themeToggleButton;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    private JLabel title;
    private JPanel formPanel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private buttonCreator loginBtn;
    private buttonCreator registerBtn;
    private JButton toggleThemeBtn;
    private JLabel logoLabel;
    private final Mainframe frame;

    public Login(Mainframe frame) {
        setLayout(new GridBagLayout());
        this.frame = frame;

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoLabel.setIcon(
                iconCreator.getIcon(
                        frame.isDarkMode() ? "Icons/logoDarkMode.svg" : "Icons/logo.svg",
                        150, 100
                )
        );
        formPanel.add(logoLabel, gbc);

        gbc.gridy++;
        title = new JLabel("Login");
        formPanel.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField();
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        loginBtn = new buttonCreator("Login", "Button.font", () -> frame.showCard("LANDING"));
        registerBtn = new buttonCreator("Register", "Button.font", () -> frame.showCard("REGISTER"));
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        formPanel.add(buttonPanel, gbc);

        gbc.gridy++;
        toggleThemeBtn = new themeToggleButton(frame::toggleTheme);
        formPanel.add(toggleThemeBtn, gbc);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.anchor = GridBagConstraints.NORTH;
        outer.weightx = 1.0;
        outer.weighty = 1.0;
        outer.insets = new Insets(40, 0, 0, 0);
        add(formPanel, outer);

        updateUI();
    }

    @Override
    public void updateUI() {
        super.updateUI();

        setBackground(UIManager.getColor("Panel.background"));
        if (formPanel != null) formPanel.setBackground(UIManager.getColor("Panel.background"));

        // Refresh logo according to theme
        if (logoLabel != null) {
            logoLabel.setIcon(
                    iconCreator.getIcon(
                            frame.isDarkMode() ? "Icons/logos/logoDarkMode.svg" : "Icons/logos/logo.svg",
                            150, 100
                    )
            );
        }

        if (title != null) {
            title.setFont(UIManager.getFont("Title.font"));
            title.setForeground(UIManager.getColor("headerColor"));
        }

        if (usernameLabel != null) {
            usernameLabel.setFont(UIManager.getFont("defaultFont"));
            usernameLabel.setForeground(UIManager.getColor("Label.foreground"));
        }
        if (usernameField != null) usernameField.setFont(UIManager.getFont("defaultFont"));

        if (passwordLabel != null) {
            passwordLabel.setFont(UIManager.getFont("defaultFont"));
            passwordLabel.setForeground(UIManager.getColor("Label.foreground"));
        }
        if (passwordField != null) passwordField.setFont(UIManager.getFont("defaultFont"));

        if (loginBtn != null) loginBtn.updateUI();
        if (registerBtn != null) registerBtn.updateUI();

        if (toggleThemeBtn != null) {
            toggleThemeBtn.setFont(UIManager.getFont("Button.font"));
            Color fg = UIManager.getColor("Button.foreground");
            if (fg != null) toggleThemeBtn.setForeground(fg);
        }
    }
}