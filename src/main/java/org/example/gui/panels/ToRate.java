package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.ratings.RatingCard;

public class ToRate extends JPanel {
    private JLabel titleLabel;
    private JLabel backLabel;
    private JPanel containerPanel; // holds the ratingcard components
    private JScrollPane scrollPane; // reference for scrolling fix
    private Landing landing;

    public ToRate(Landing landing) { // accept landing for navigation
        this.landing = landing;
        initComponents();
    }

    private void initComponents() {
        removeAll();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        // back button
        backLabel = new JLabel("< Back");
        fontManager.applyHeading(backLabel, 8);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ToRate.this.landing != null) {
                    ToRate.this.landing.showCard("PROFILE");
                } else {
                    System.out.println("landing reference is null. cannot navigate.");
                }
                backLabel.requestFocusInWindow();
            }
        });
        topPanel.add(backLabel);
        topPanel.add(Box.createVerticalStrut(20));

        titleLabel = new JLabel("My Ratings");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(20));

        // scrollable area
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));


        scrollPane = new JScrollPane(containerPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        addRatingCard("Real Laundromat", "001");
        addRatingCard("AllKlean Laundry", "003");
        addRatingCard("Real Laundromat", "005");
        addRatingCard("Real Laundromat", "004");
        addRatingCard("AllKlean Laundromat", "006");

        revalidate();
        repaint();
    }

    public void addRatingCard(String laundromatName, String orderId) {
        RatingCard card = new RatingCard(laundromatName, orderId);
        containerPanel.add(card);
        containerPanel.add(Box.createVerticalStrut(10)); // spacer between cards
        containerPanel.revalidate();
        containerPanel.repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> {
            if (titleLabel != null) {
                fontManager.applyHeading(titleLabel, 5);
            }
            if (backLabel != null) {
                fontManager.applyHeading(backLabel, 8);
            }

            if (scrollPane != null) {
                scrollPane.revalidate();
                scrollPane.repaint();
            }

            repaint();
        });
    }
}