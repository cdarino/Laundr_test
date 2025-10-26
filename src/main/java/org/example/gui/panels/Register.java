package org.example.gui.panels;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;
import org.example.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

public class Register extends JPanel {
    private final Mainframe frame;
    private JLabel title, userLabel, emailLabel, phoneLabel, addressLabel, passLabel, confirmLabel;
    private JTextField userField, emailField, phoneField, addressField;
    private JPasswordField passField, confirmField;
    private buttonCreator registerBtn;
    private roundedPanel card;
    private JLabel goBackLabel;

    public Register(Mainframe frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("background"));

        card = new roundedPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(UIManager.getColor("Register.background"));
        card.setBorder(new roundedBorder(30, UIManager.getColor("listBorder"), 0));
        card.setPreferredSize(new Dimension(450, 580));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;

        goBackLabel = new JLabel("< Back");
        goBackLabel.setFont(UIManager.getFont("Button.font"));
        goBackLabel.setForeground(UIManager.getColor("Label.foreground"));
        goBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        goBackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.showCard("LOGIN");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                goBackLabel.setForeground(new Color(26, 82, 118));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                goBackLabel.setForeground(new Color(41, 128, 185));
            }
        });

        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 0, 0);
        card.add(goBackLabel, gbc);

        title = new JLabel("Register", SwingConstants.CENTER);
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(title, gbc);

        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        userLabel = new JLabel("Username");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(userLabel, gbc);

        userField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(userField, gbc);

        emailLabel = new JLabel("Email Address");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(emailField, gbc);

        phoneLabel = new JLabel("Phone Number");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(phoneField, gbc);

        addressLabel = new JLabel("Address");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(addressLabel, gbc);

        addressField = new JTextField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(addressField, gbc);

        passLabel = new JLabel("Password");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(passLabel, gbc);

        passField = new JPasswordField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 15, 20);
        card.add(passField, gbc);

        confirmLabel = new JLabel("Confirm Password");
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 2, 20);
        card.add(confirmLabel, gbc);

        confirmField = new JPasswordField(20);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 20, 25, 20);
        card.add(confirmField, gbc);

        registerBtn = new buttonCreator("Register", "Button.font", this::handleRegister);
        registerBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 20, 0);
        card.add(registerBtn, gbc);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.anchor = GridBagConstraints.CENTER;
        add(card, outer);

        applyStyles();
    }

    private void handleRegister() {
        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username must be at least 6 characters long and contain only letters, digits, underscores (_), or dashes (-).",
                    "Invalid Username",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your address.",
                    "Missing Address",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CustomerDAO dao = new CustomerDAO(conn);
            Customer customer = new Customer(username, password, phone, address, email);

            boolean success = dao.registerCustomer(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.showCard("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Username or email already exists.",
                        "Registration Error",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[A-Za-z0-9_-]{6,}$");
    }

    private void applyStyles() {
        setBackground(UIManager.getColor("Register.background") != null
                ? UIManager.getColor("Register.background")
                : new Color(52, 152, 219));

        card.setBackground(UIManager.getColor("Register.panel"));

        title.setFont(UIManager.getFont("Title.font"));
        title.setForeground(UIManager.getColor("headerColor"));

        JLabel[] labels = {userLabel, emailLabel, phoneLabel, addressLabel, passLabel, confirmLabel};
        for (JLabel label : labels) {
            label.setFont(UIManager.getFont("Button.font"));
            label.setForeground(UIManager.getColor("Label.foreground"));
        }

        JTextField[] fields = {userField, emailField, phoneField, addressField};
        for (JTextField field : fields) {
            field.setFont(UIManager.getFont("defaultFont"));
            field.setBorder(BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1));
        }

        JPasswordField[] passFields = {passField, confirmField};
        for (JPasswordField pf : passFields) {
            pf.setFont(UIManager.getFont("defaultFont"));
            pf.setBorder(BorderFactory.createLineBorder(UIManager.getColor("listBorder"), 1));
        }

        registerBtn.setBackground(UIManager.getColor("Button.background"));
        registerBtn.setForeground(UIManager.getColor("Button.foreground"));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (card != null) {
            SwingUtilities.invokeLater(this::applyStyles);
        }
    }
}
