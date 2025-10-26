package org.example.gui.utils.dashboard;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;

public class welcomeCard extends JPanel {
    private static final int CARD_HEIGHT = 180;

    private JLabel welcome;
    private JLabel user;

    public welcomeCard(String username) {
        setOpaque(false);
        setLayout(new BorderLayout());

        roundedPanel card = new roundedPanel();
        card.setLayout(new BorderLayout());
        card.setBackgroundColorKey("Menu.background");
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        welcome = new JLabel("Welcome back,");
        fontManager.applyHeading(welcome, 1);

        user = new JLabel(username + " !");
        fontManager.applyHeading(user, 2);

        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(user);

        card.add(textPanel, BorderLayout.WEST);

        Icon welcomeIcon = iconCreator.getIcon("Icons/welcomeDesign.svg", 210, 130);
        JLabel iconLabel = new JLabel(welcomeIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel, BorderLayout.NORTH);

        card.add(iconPanel, BorderLayout.EAST);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_HEIGHT));
        card.setPreferredSize(new Dimension(0, CARD_HEIGHT));

        add(card, BorderLayout.CENTER);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (welcome != null) fontManager.applyHeading(welcome, 1);
        if (user != null) fontManager.applyHeading(user, 2);
        repaint();
    }

    public void setUsername(String username) {
        if (user != null) user.setText(username + " !");
    }
}

