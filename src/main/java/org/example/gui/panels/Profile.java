package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Profile extends JPanel {
    private final Mainframe frame;
    private final Landing landing;

    private String getIconPath(String iconName) {
        String theme = Mainframe.dark ? "darkmode" : "lightmode";
        return "Icons/" + theme + "/" + iconName;
    }

    public Profile(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));

        add(createTopSection());
        add(Box.createVerticalStrut(60));
        add(createMiddleSection());
        add(Box.createVerticalStrut(40));
        add(createBottomSection());
    }

    //top
    private JPanel createTopSection() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        namePanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        String username = frame.getCurrentUser();
        JLabel nameLabel = new JLabel(username);
        fontManager.applyHeading(nameLabel, 5);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel editProfileLabel = new JLabel("Edit Profile");
        fontManager.applyHeading(editProfileLabel, 6);
        editProfileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        editProfileLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        editProfileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                landing.showCard("EDIT");
            }
        });

        editProfileLabel.setIcon(iconCreator.getIcon(getIconPath("edit.svg"), 18, 18));
        editProfileLabel.setIconTextGap(6);
        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(6));
        namePanel.add(editProfileLabel);

        topPanel.add(namePanel);

        return topPanel;
    }

    //mid
    private JPanel createMiddleSection() {
        roundedPanel middlePanel = new roundedPanel();
        middlePanel.setLayout(new GridLayout(1, 4, 20, 0));
        middlePanel.setBackground(UIManager.getColor("Profile.background"));
        middlePanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(20, UIManager.getColor("listBorder"), 1),
                BorderFactory.createEmptyBorder(40, 20, 20, 20)
        ));

        middlePanel.add(createIconLabelPanel(getIconPath("wallet.svg"), "Digital Wallet"));
        middlePanel.add(createIconLabelPanel(getIconPath("toReceive.svg"), "To Receive"));
        middlePanel.add(createIconLabelPanel(getIconPath("star.svg"), "To Rate"));

        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        return middlePanel;
    }

    //bottom
    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        roundedPanel supportPanel = createSupportPanel();
        supportPanel.setPreferredSize(new Dimension(450, 160));
        supportPanel.setMaximumSize(new Dimension(450, 160));
        bottomPanel.add(supportPanel);

        JPanel fillerPanel = new JPanel();
        fillerPanel.setOpaque(false);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(fillerPanel);

        return bottomPanel;
    }

    //bottom right panel
    private roundedPanel createSupportPanel() {
        roundedPanel supportPanel = new roundedPanel();
        supportPanel.setLayout(new BoxLayout(supportPanel, BoxLayout.Y_AXIS));
        supportPanel.setBackground(UIManager.getColor("Profile.background"));
        supportPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(20, UIManager.getColor("listBorder"), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        supportPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel infoLabel = new JLabel("Information");
        fontManager.applyHeading(infoLabel, 6);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(infoLabel);

        supportPanel.add(titlePanel);
        supportPanel.add(Box.createVerticalStrut(15));
        supportPanel.add(createSupportItem(getIconPath("email.svg"), "laundr@gmail.com"));
        supportPanel.add(Box.createVerticalStrut(10));
        supportPanel.add(createSupportItem(getIconPath("phone.svg"), "09887654322 / 274-982"));
        supportPanel.add(Box.createVerticalGlue());

        return supportPanel;
    }

    //helper for middle and bottom left icons
    private JPanel createIconLabelPanel(String iconPath, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(iconCreator.getIcon(iconPath, 45, 45));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fontManager.applyHeading(textLabel, 6);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(textLabel);

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (text) {
                    case "Saved Laundromats":
                        landing.showCard("LAUNDROMATS");
                        System.out.println("laundromat");
                        break;
                    case "Digital Wallet":
                        landing.showCard("WALLET");
                        System.out.println("wallet");
                        break;
                    case "To Receive":
                        landing.showCard("RECEIVE");
                        System.out.println("receive");
                        break;
                    case "To Rate":
                        landing.showCard("RATE");
                        System.out.println("rate");
                        break;
                    default:
                        System.out.println("Unknown card: " + text);
                        break;
                }
            }
        });

        return panel;
    }

    //helper for support icons
    private JPanel createSupportItem(String iconPath, String text) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        itemPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(iconCreator.getIcon(iconPath, 20, 20));
        JLabel textLabel = new JLabel(text);
        fontManager.applyHeading(textLabel, 6);

        itemPanel.add(iconLabel);
        itemPanel.add(textLabel);

        return itemPanel;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // refresh icons
        SwingUtilities.invokeLater(() -> {
            removeAll();
            add(createTopSection());
            add(Box.createVerticalStrut(60));
            add(createMiddleSection());
            add(Box.createVerticalStrut(40));
            add(createBottomSection());
            revalidate();
            repaint();
        });
    }
}
