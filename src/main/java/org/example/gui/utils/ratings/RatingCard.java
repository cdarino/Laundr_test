package org.example.gui.utils.ratings;

// new imports
import org.example.database.DBConnect;
import org.example.database.ReviewDAO;
import org.example.models.ReviewData; // --- fix: use new models package ---
//
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Vector;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;

public class RatingCard extends roundedPanel {

    private JLabel laundromatNameLabel;
    private JLabel orderIdLabel;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewTextArea;
    private buttonCreator saveButton;

    private final int orderID;
    private final int laundromatID;
    private final int custID;
    private final ReviewData existingReview; // will be null if no review exists
    private final Runnable saveCallback; // action to run on successful save

    public RatingCard(int orderID, int laundromatID, int custID, String laundromatName, ReviewData existingReview, Runnable saveCallback) {
        super(15);
        this.orderID = orderID;
        this.laundromatID = laundromatID;
        this.custID = custID;
        this.existingReview = existingReview;
        this.saveCallback = saveCallback;

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

        // --- row 0: name and rating ---
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        laundromatNameLabel = new JLabel(laundromatName);
        add(laundromatNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        // create a vector with 1, 2, 3, 4, 5 and a "select" prompt
        Vector<Integer> ratings = new Vector<>();
        ratings.add(null); // placeholder for "select"
        for (int i = 5; i >= 1; i--) ratings.add(i);

        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setRenderer(new RatingRenderer()); // custom renderer for "select"
        ratingComboBox.setSelectedIndex(0); // default to "select"
        add(ratingComboBox, gbc);

        // --- row 1: order id ---
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        orderIdLabel = new JLabel("Order #" + orderID);
        add(orderIdLabel, gbc);

        // --- row 2: text area ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        reviewTextArea = new JTextArea("Write your review...");
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        // add placeholder text functionality
        reviewTextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (reviewTextArea.getText().equals("Write your review...")) {
                    reviewTextArea.setText("");
                    reviewTextArea.setForeground(UIManager.getColor("TextArea.foreground"));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (reviewTextArea.getText().isEmpty()) {
                    reviewTextArea.setForeground(Color.GRAY);
                    reviewTextArea.setText("Write your review...");
                }
            }
        });
        add(new JScrollPane(reviewTextArea), gbc);

        // --- row 3: save button ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        saveButton = new buttonCreator("Save", "Button.font", this::handleSaveReview);
        add(saveButton, gbc);

        // --- check if review already exists ---
        if (existingReview != null) {
            populateExistingReview();
        }

        updateUI(); // apply fonts and colors
    }

    // --- new method to handle saving ---
    private void handleSaveReview() {
        Integer rating = getSelectedRating();
        String comment = getReviewText();

        if (rating == null) {
            JOptionPane.showMessageDialog(this, "Please select a rating (1-5).", "Rating Required", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnect.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Database connection lost.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ReviewDAO reviewDAO = new ReviewDAO(conn);
            boolean success = reviewDAO.addReviewAndUpdateAverage(orderID, laundromatID, custID, rating, comment);

            if (success) {
                JOptionPane.showMessageDialog(this, "Review saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // disable card and run callback
                setCardEnabled(false);
                if (saveCallback != null) {
                    saveCallback.run(); // this will tell torate panel to refresh
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save review.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving review: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- new method to disable card ---
    private void setCardEnabled(boolean enabled) {
        ratingComboBox.setEnabled(enabled);
        reviewTextArea.setEnabled(enabled);
        saveButton.setEnabled(enabled);

        if (!enabled) {
            reviewTextArea.setEditable(false);
            Color disabledColor = UIManager.getColor("Label.disabledForeground");
            reviewTextArea.setForeground(disabledColor);

            // disable the buttonCreator
            saveButton.setEnabled(false);
            saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // --- new method to populate existing data ---
    private void populateExistingReview() {
        ratingComboBox.setSelectedItem(existingReview.getRating());
        reviewTextArea.setText(existingReview.getComment());
        reviewTextArea.setForeground(UIManager.getColor("TextArea.foreground"));

        // disable everything
        setCardEnabled(false);
    }

    // custom renderer for the jcombobox
    class RatingRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText("Select Rating");
            } else {
                setText(value.toString());
            }
            return this;
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // apply fonts and colors when theme changes
        SwingUtilities.invokeLater(() -> {
            if (laundromatNameLabel != null) {
                fontManager.applyHeading(laundromatNameLabel, 9);
                fontManager.applyHeading(orderIdLabel, 13);
                ratingComboBox.setFont(fontManager.h8());
                reviewTextArea.setFont(fontManager.h8());

                if (reviewTextArea.getText().equals("Write your review...")) {
                    reviewTextArea.setForeground(Color.GRAY);
                } else {
                    // only set foreground if not disabled
                    if (reviewTextArea.isEnabled()) {
                        reviewTextArea.setForeground(UIManager.getColor("TextArea.foreground"));
                    }
                }
            }

            if (saveButton != null) {
                saveButton.updateUI();
                // re-apply disabled state if necessary
                if (existingReview != null) {
                    setCardEnabled(false);
                }
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

    public Integer getSelectedRating() {
        Object selected = ratingComboBox.getSelectedItem();
        return (selected instanceof Integer) ? (Integer) selected : null;
    }

    public String getReviewText() {
        return reviewTextArea.getText().equals("Write your review...") ? "" : reviewTextArea.getText();
    }
}