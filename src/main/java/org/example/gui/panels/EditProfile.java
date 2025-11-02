package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.editprofile.profileCard;
import org.example.gui.utils.editprofile.addressCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditProfile extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Mainframe frame;
    private Landing landing;

    public EditProfile(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;
        initComponents();
    }

    private String getIconPath(String iconName) {
        String theme = Mainframe.dark ? "darkmode" : "lightmode";
        return "Icons/" + theme + "/" + iconName;
    }

    private void initComponents() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setBackground(UIManager.getColor("Panel.background"));

        // left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(250, 400));
        leftPanel.setMaximumSize(new Dimension(250, 400));

        // back button
        JLabel backLabel = new JLabel("< Back");
        fontManager.applyHeading(backLabel, 8);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                landing.showCard("PROFILE");
            }
        });
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(backLabel);
        leftPanel.add(Box.createVerticalStrut(20));

        // username & edit profile
        String username = frame.getCurrentUser();
        JLabel usernameLabel = new JLabel(username);
        fontManager.applyHeading(usernameLabel, 5);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel editProfileLabel = new JLabel("Edit Profile");
        fontManager.applyHeading(editProfileLabel, 6);
        editProfileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        editProfileLabel.setIcon(iconCreator.getIcon(getIconPath("edit.svg"), 16, 16));
        editProfileLabel.setIconTextGap(6);
        editProfileLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editProfileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "PROFILE");
            }
        });

        leftPanel.add(usernameLabel);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(editProfileLabel);
        leftPanel.add(Box.createVerticalStrut(25));

        // nav tabs
        JLabel profileTab = new JLabel("Profile");
        fontManager.applyHeading(profileTab, 8);
        profileTab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "PROFILE");
            }
        });
        profileTab.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(profileTab);
        leftPanel.add(Box.createVerticalStrut(10));

        JLabel addressTab = new JLabel("Address");
        fontManager.applyHeading(addressTab, 8);
        addressTab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addressTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "ADDRESS");
            }
        });
        addressTab.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(addressTab);

        // wrap left panel to top
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(leftPanel, BorderLayout.NORTH);

        // right card panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(new profileCard(frame), "PROFILE");
        cardPanel.add(new addressCard(frame), "ADDRESS");

        add(leftWrapper);
        add(Box.createHorizontalStrut(-500));
        add(cardPanel);

        cardLayout.show(cardPanel, "PROFILE");
        revalidate();
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> {
            // remember current card
            String currentCard = "PROFILE";
            for (Component comp : cardPanel.getComponents()) {
                if (comp.isVisible()) {
                    if (comp instanceof profileCard) currentCard = "PROFILE";
                    else if (comp instanceof addressCard) currentCard = "ADDRESS";
                }
            }

            // rebuild UI
            initComponents();

            // restore current card
            cardLayout.show(cardPanel, currentCard);
        });
    }
}
