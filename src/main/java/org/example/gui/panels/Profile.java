package org.example.gui.panels;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Profile extends JPanel {

    public Profile() {
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

        ImageIcon im = new ImageIcon(getClass().getResource("/Pictures/profile pictures/pfp1.png"));
        Image img = im.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel profilePic = new JLabel(new ImageIcon(img));
        profilePic.setPreferredSize(new Dimension(90, 90));
        profilePic.setMaximumSize(new Dimension(90, 90));
        profilePic.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        namePanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("johndoe123");
        fontManager.applyHeading(nameLabel, 5);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel editProfileLabel = new JLabel("✏ Edit Profile");
        fontManager.applyHeading(editProfileLabel, 6);
        editProfileLabel.setForeground(Color.DARK_GRAY);
        editProfileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        editProfileLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        editProfileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("click");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                editProfileLabel.setForeground(new Color(30, 144, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                editProfileLabel.setForeground(Color.DARK_GRAY);
            }
        });

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(6));
        namePanel.add(editProfileLabel);

        topPanel.add(profilePic);
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

        middlePanel.add(createIconLabelPanel("Icons/lightmode/bookmark.svg", "Saved Laundromats"));
        middlePanel.add(createIconLabelPanel("Icons/lightmode/wallet.svg", "Digital Wallet"));
        middlePanel.add(createIconLabelPanel("Icons/lightmode/toReceive.svg", "To Receive"));
        middlePanel.add(createIconLabelPanel("Icons/lightmode/star.svg", "To Rate"));

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

    //saved panel (the bottom left)
    private roundedPanel createSavedPanel() {
        roundedPanel savedPanel = new roundedPanel();
        savedPanel.setLayout(new BoxLayout(savedPanel, BoxLayout.Y_AXIS));
        savedPanel.setBackground(UIManager.getColor("Profile.background"));
        savedPanel.setBorder(BorderFactory.createCompoundBorder(
                new roundedBorder(20, UIManager.getColor("listBorder"), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        savedPanel.add(Box.createVerticalGlue());
        savedPanel.add(createIconLabelPanel("Icons/lightmode/bookmark.svg", "Saved Laundromats"));
        savedPanel.add(Box.createVerticalStrut(10));
        savedPanel.add(Box.createVerticalGlue());

        return savedPanel;
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

        JLabel supportTitle = new JLabel("Support");
        fontManager.applyHeading(supportTitle, 6);
        supportTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(supportTitle);

        supportPanel.add(titlePanel);
        supportPanel.add(Box.createVerticalStrut(15));
        supportPanel.add(createSupportItem("Icons/lightmode/help.svg", "Help Center"));
        supportPanel.add(Box.createVerticalStrut(10));
        supportPanel.add(createSupportItem("Icons/lightmode/info.svg", "About Laundr"));
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

        return panel;
    }

    //support icons
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
}
