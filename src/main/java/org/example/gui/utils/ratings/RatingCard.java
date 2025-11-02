package org.example.gui.utils.ratings;

import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Vector;

public class RatingCard extends roundedPanel {

    private JLabel laundromatNameLabel;
    private JLabel orderIdLabel;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewTextArea;
    private buttonCreator saveButton;

    public RatingCard(String laundromatName, String orderId) {
        super(15);
        setBackground(UIManager.getColor("Profile.background"));

        Color borderColor = UIManager.getColor("listBorder");

        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 2, true), // dynamic border color
                new EmptyBorder(15, 20, 15, 20) // inner padding
        ));

        setLayout(new GridBagLayout());
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // fixed height, flexible width

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST; // anchor all components to top-left

        // column container (name, id, and rating)
        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
        leftContentPanel.setOpaque(false);

        // laundromat name
        laundromatNameLabel = new JLabel(laundromatName);
        fontManager.applyHeading(laundromatNameLabel, 9);
        laundromatNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftContentPanel.add(laundromatNameLabel);
        leftContentPanel.add(Box.createVerticalStrut(5));

        // order id
        orderIdLabel = new JLabel("Order " + orderId);
        fontManager.applyHeading(orderIdLabel, 13);
        orderIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftContentPanel.add(orderIdLabel);
        leftContentPanel.add(Box.createVerticalStrut(10));

        // rating combobox
        Vector<Integer> ratings = new Vector<>();
        for (int i = 5; i >= 1; i--) {
            ratings.add(i);
        }
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setFont(fontManager.h8());
        ratingComboBox.setFocusable(false); // remove focus border
        ratingComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftContentPanel.add(ratingComboBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0; // don't allow it to grow horizontally
        gbc.weighty = 1.0; // push content down
        gbc.fill = GridBagConstraints.VERTICAL;
        add(leftContentPanel, gbc);

        // review text area
        reviewTextArea = new JTextArea("Write your review...");
        reviewTextArea.setFont(fontManager.h8());
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        reviewTextArea.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        reviewTextArea.setOpaque(false);
        reviewTextArea.setBackground(new Color(0, 0, 0, 0));

        // wrap the jtextarea in a jscrollpane
        JScrollPane reviewScrollPane = new JScrollPane(reviewTextArea);
        reviewScrollPane.setBorder(BorderFactory.createEmptyBorder()); // remove default scroll pane border
        reviewScrollPane.setPreferredSize(new Dimension(300, 100)); // preferred size
        reviewScrollPane.setMinimumSize(new Dimension(200, 80)); // minimum size
        reviewScrollPane.getVerticalScrollBar().setUnitIncrement(10); // smooth scrolling

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0; // allow it to take all available horizontal space
        gbc.weighty = 1.0; // allow it to take all available vertical space
        gbc.fill = GridBagConstraints.BOTH; // fill both horizontal and vertical space
        gbc.insets = new Insets(5, 30, 5, 5);
        add(reviewScrollPane, gbc);

        // save btn
        saveButton = new buttonCreator("Save", "Button.font", () -> {
            // add save logic here
            System.out.println("review saved: " + getReviewText() + " with rating: " + getSelectedRating());
        });

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0; // don't push the button wide
        gbc.weighty = 0; // don't push the button vertically
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHEAST; // anchor to bottom right
        gbc.insets = new Insets(0, 30, 0, 5);
        add(saveButton, gbc);

        reviewTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (reviewTextArea.getText().equals("Write your review...")) {
                    reviewTextArea.setText("");
                    reviewTextArea.setForeground(UIManager.getColor("TextArea.foreground"));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (reviewTextArea.getText().trim().isEmpty()) {
                    reviewTextArea.setText("Write your review...");
                    reviewTextArea.setForeground(Color.GRAY);
                }
            }
        });
        reviewTextArea.setForeground(Color.GRAY); // initial gray text

        updateUI(); // apply initial ui settings
    }

    @Override
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> {
            // re-apply fonts and colors when theme changes
            if (laundromatNameLabel != null) {
                fontManager.applyHeading(laundromatNameLabel, 7);
                fontManager.applyHeading(orderIdLabel, 13);
                ratingComboBox.setFont(fontManager.h8());
                reviewTextArea.setFont(fontManager.h8());

                if (reviewTextArea.getText().equals("Write your review...")) {
                    reviewTextArea.setForeground(Color.GRAY);
                } else {
                    reviewTextArea.setForeground(UIManager.getColor("TextArea.foreground"));
                }
            }

            if (saveButton != null) {
                saveButton.updateUI();
            }

            Color borderColor = UIManager.getColor("listBorder");

            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderColor, 2, true),
                    new EmptyBorder(15, 20, 15, 20)
            ));

            revalidate();
            repaint();
        });
    }

    // add getters for reviewtext and selectedrating
    public Integer getSelectedRating() {
        Object selected = ratingComboBox.getSelectedItem();
        return (selected instanceof Integer) ? (Integer) selected : null;
    }

    public String getReviewText() {
        return reviewTextArea.getText().equals("Write your review...") ? "" : reviewTextArea.getText();
    }
}