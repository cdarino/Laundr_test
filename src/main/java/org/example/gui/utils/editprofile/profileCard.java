package org.example.gui.utils.editprofile;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontManager;
import org.example.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException; // imported for better error handling

public class profileCard extends JPanel {

    private CustomerDAO customerDAO;
    private Customer currentCustomer;
    private JTextField tfUsername;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JButton saveBtn;
    private final Mainframe frame;
    // ---

    public profileCard(Mainframe frame) {
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

        // profile panel
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
        JLabel profileTitle = new JLabel("My Profile");
        fontManager.applyHeading(profileTitle, 9);
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(profileTitle, gbc);

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

        // Username
        gbc.gridy = 2; // changed from gbc.gridy++
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblUsername = new JLabel("Username:");
        fontManager.applyHeading(lblUsername, 8);
        lblUsername.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblUsername, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfUsername = new JTextField(); // using class field
        tfUsername.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfUsername.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfUsername.setBackground(UIManager.getColor("TextField.background"));
        tfUsername.setEditable(true); // username can be changed
        formPanel.add(tfUsername, gbc);

        // Email
        gbc.gridy++; // this is gridy 3
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblEmail = new JLabel("Email:");
        fontManager.applyHeading(lblEmail, 8);
        lblEmail.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfEmail = new JTextField(); // using class field
        tfEmail.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfEmail.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfEmail.setBackground(UIManager.getColor("TextField.background"));
        formPanel.add(tfEmail, gbc);

        // phone num
        gbc.gridy++; // this is gridy 4
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblPhone = new JLabel("Phone Number:");
        fontManager.applyHeading(lblPhone, 8);
        lblPhone.setPreferredSize(new Dimension(labelWidth, 28));
        formPanel.add(lblPhone, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfPhone = new JTextField(); // using class field
        tfPhone.setPreferredSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfPhone.setMaximumSize(new Dimension(txtFieldWidth, txtFieldHeight));
        tfPhone.setBackground(UIManager.getColor("TextField.background"));
        formPanel.add(tfPhone, gbc);

        // pushes save button down a bit
        gbc.gridy++; // this is gridy 5
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalStrut(8), gbc);

        // save button row aligned to right
        gbc.gridy++; // this is gridy 6
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false);
        saveBtn = new JButton("Save"); // using class field
        saveBtn.setPreferredSize(new Dimension(120, 35));

        // --- added save action ---
        saveBtn.addActionListener(e -> handleSaveProfile());
        // ---

        btnWrap.add(saveBtn);
        formPanel.add(btnWrap, gbc);

        add(formPanel);

        // --- load data ---
        // also, add a listener to refresh data when the panel is shown
        // this fixes the data not loading when you log out and log in as someone else
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent e) {
                // this is called when the panel is shown
                loadCustomerData(frame.getCurrentUser());
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent e) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent e) {}
        });
    }

    private void loadCustomerData(String username) {
        if (customerDAO == null) {
            System.err.println("profilecard: customerdao is null");
            return;
        }

        currentCustomer = customerDAO.getCustomerByUsername(username);
        if (currentCustomer != null) {
            tfUsername.setText(currentCustomer.getUsername());
            tfEmail.setText(currentCustomer.getEmail());
            tfPhone.setText(currentCustomer.getPhone());
        } else {
            tfUsername.setText("");
            tfEmail.setText("");
            tfPhone.setText("");
            if (username != null) {
                JOptionPane.showMessageDialog(this, "Could not load user data for: " + username, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // save data to db
    private void handleSaveProfile() {
        if (currentCustomer == null || customerDAO == null) {
            JOptionPane.showMessageDialog(this, "Error: Cannot save data. customer object is null.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newUsername = tfUsername.getText().trim();
        String newEmail = tfEmail.getText().trim();
        String newPhone = tfPhone.getText().trim();
        String oldUsername = currentCustomer.getUsername();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // step 1: check if username was changed and if the new one already exists
            if (!newUsername.equals(oldUsername) && customerDAO.usernameExists(newUsername)) {
                JOptionPane.showMessageDialog(this,
                        "Username '" + newUsername + "' already exists. Please choose another.",
                        "Username Taken",
                        JOptionPane.ERROR_MESSAGE);
                return; // stop the save
            }

            // step 2: proceed with the update using the customer id
            boolean success = customerDAO.updateProfile(
                    currentCustomer.getCustID(), // use id to find the user
                    newUsername,
                    newEmail,
                    newPhone
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // update local customer object
                currentCustomer.setUsername(newUsername);
                currentCustomer.setEmail(newEmail);
                currentCustomer.setPhone(newPhone);


                // update the mainframe's current user if the username changed
                if (!newUsername.equals(oldUsername)) {
                    this.frame.setCurrentUser(newUsername);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}