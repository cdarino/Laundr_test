package org.example.gui.utils.creators;

import javax.swing.*;
import java.awt.*;

import org.example.gui.Mainframe;

public class adminHeaderCreator extends JPanel {
    private final Mainframe frame;
    private final JButton logoutBtn;
    private final JButton themeToggleBtn;
    private final JLabel logoLabel;

    public adminHeaderCreator(Mainframe frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        logoutBtn = new JButton();
        logoutBtn.setPreferredSize(new Dimension(44, 44));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            frame.logout();
        });

        leftPanel.add(logoutBtn);

        logoLabel = new JLabel(iconCreator.getIcon("Icons/logos/logoWhite.svg", 70, 50));
        leftPanel.add(logoLabel);

        themeToggleBtn = new themeToggleButton(frame::toggleTheme);

        add(leftPanel, BorderLayout.WEST);
        add(themeToggleBtn, BorderLayout.EAST);

        updateUI();
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if (logoutBtn != null) {
            logoutBtn.setIcon(iconCreator.getIcon("Icons/logout.svg", 28, 28));
            logoutBtn.setToolTipText("Logout");
        }
            setBackground(UIManager.getColor("Menu.background"));
            setBorder(BorderFactory.createMatteBorder(
                    0, 0, 2, 0,
                    UIManager.getColor("Menu.borderColor")
            ));
            setForeground(UIManager.getColor("Menu.foreground"));

            if (themeToggleBtn != null) {
                themeToggleBtn.setFont(UIManager.getFont("Button.font"));
                Color fg = UIManager.getColor("Button.foreground");
                if (fg != null) themeToggleBtn.setForeground(fg);
            }

            if (logoLabel != null) {
                logoLabel.setIcon(iconCreator.getIcon(
                        frame.isDarkMode() ? "Icons/logos/logoDarkMode.svg" : "Icons/logos/logoWhite.svg",
                        70, 50
                ));
        }
    }
}