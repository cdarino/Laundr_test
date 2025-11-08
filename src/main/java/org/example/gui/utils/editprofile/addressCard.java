package org.example.gui.utils.editprofile;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;
import org.example.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class addressCard extends JPanel {

    private CustomerDAO customerDAO;
    private Customer currentCustomer;
    private JTextField tfAddress1;
    private JButton saveBtn;
    private final Mainframe frame;

    public addressCard(Mainframe frame) {
        this.frame = frame; // store frame reference

        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                customerDAO = new CustomerDAO(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setMaximumSize(new Dimension(850, 500)); // container max size

        // form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIManager.getColor("Profile.background"));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(850, 370));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title row (span two columns)
        JLabel title = new JLabel("Shipping Address");
        fontManager.applyHeading(title, 9);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(title, gbc);

        // separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(sep, gbc);

        // reset for fields
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int labelWidth = 140;
        int txtFieldWidth = 180;
        int txtFieldHeight = 40;

        // Shipping Address 1
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblAddress1 = new JLabel("Shipping Address:"); // changed label
        fontManager.applyHeading(lblAddress1, 8);
        lblAddress1.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblAddress1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfAddress1 = new JTextField(); // using class field
        tfAddress1.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfAddress1.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        formPanel.add(tfAddress1, gbc);

        // push button row down a bit
        gbc.gridy++; // this was gbc.gridy = 3
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0; // add weighty to push button to bottom
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalStrut(8), gbc);

        // save button aligned to right
        gbc.gridy++; // this was gbc.gridy = 4
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0; // reset weighty
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        saveBtn = new JButton("Save"); // using class field
        saveBtn.setPreferredSize(new Dimension(120, 35));

        // --- added save action ---
        saveBtn.addActionListener(e -> handleSaveAddress());

        btnWrap.add(saveBtn);
        formPanel.add(btnWrap, gbc);

        add(formPanel);

        // --- load data ---
        // also, add a listener to refresh data when the panel is shown
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent e) {
                // this is called when the panel is shown
                loadCustomerData(frame.getCurrentUser());
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent e) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent e) {}
        });
    }

    // load data from db
    private void loadCustomerData(String username) {
        if (customerDAO == null) {
            System.err.println("addresscard: customerdao is null");
            return;
        }

        currentCustomer = customerDAO.getCustomerByUsername(username);
        if (currentCustomer != null) {
            tfAddress1.setText(currentCustomer.getAddress());
        } else {
            // clear field if user not found
            tfAddress1.setText("");
            JOptionPane.showMessageDialog(this, "Could not load user data for: " + username, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // save data to db
    private void handleSaveAddress() {
        if (currentCustomer == null || customerDAO == null) {
            JOptionPane.showMessageDialog(this, "Error: Cannot save data. customer object is null.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newAddress = tfAddress1.getText().trim();

        try {
            // we use the username to update the address
            boolean success = customerDAO.updateAddress(currentCustomer.getUsername(), newAddress);
            if (success) {
                JOptionPane.showMessageDialog(this, "Address updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // update local customer object
                currentCustomer.setAddress(newAddress);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update address.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving address: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}