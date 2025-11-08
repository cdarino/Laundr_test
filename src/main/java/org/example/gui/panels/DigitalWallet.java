package org.example.gui.panels;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.buttonCreator;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;

public class DigitalWallet extends JPanel {

    private Mainframe frame;
    private Landing landing;

    private JLabel backLabel;
    private JLabel titleLabel;
    private roundedPanel balancePanel;
    private JLabel availableLabel;
    private JLabel balanceLabel;
    private buttonCreator cashInButton;
    private roundedPanel amountPanel;
    private JLabel amountTitle;
    private JTextField amountField;
    private buttonCreator confirmButton;

    private CustomerDAO customerDAO;
    private int currentCustID = -1;

    public DigitalWallet(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;
        //initialize dao
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                customerDAO = new CustomerDAO(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponents();

        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent e) {
                // this is called when the panel is shown
                loadBalance();
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent e) {
                // reset cust id when panel is hidden
                currentCustID = -1;
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent e) {}
        });
    }

    private void loadBalance() {
        if (customerDAO == null) {
            balanceLabel.setText("P--.--");
            System.err.println("digitalwallet: customerdao is null.");
            return;
        }
        String username = frame.getCurrentUser();
        if (username == null) {
            balanceLabel.setText("P--.--");
            return;
        }

        try {
            // get custid if we don't have it
            if (currentCustID == -1) {
                currentCustID = customerDAO.getCustomerId(username);
            }

            if (currentCustID != -1) {
                // fetch the balance
                double balance = customerDAO.getWalletBalance(currentCustID);
                String balanceText = String.format("P %.2f", balance);
                balanceLabel.setText(balanceText);
            } else {
                balanceLabel.setText("P--.--");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            balanceLabel.setText("P ERROR");
        }
    }

    private void initComponents() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setBackground(UIManager.getColor("Panel.background"));

        backLabel = new JLabel("< Back"); // use field
        fontManager.applyHeading(backLabel, 8);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                landing.showCard("PROFILE");
            }
        });
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(backLabel);
        add(Box.createVerticalStrut(20));

        titleLabel = new JLabel("Digital Wallet"); // use field
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        amountPanel = createAmountPanel(); // use field
        balancePanel = createBalancePanel(); // use field

        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        balancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        add(balancePanel);
        add(Box.createVerticalStrut(20));

        amountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        amountPanel.setVisible(false); // hidden unless btn is pressed
        add(amountPanel);

        // push everything to the top
        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    private roundedPanel createBalancePanel() {
        roundedPanel panel = new roundedPanel(15);
        panel.setBackgroundColorKey("Menu.background");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        // available balance lbl
        availableLabel = new JLabel("AVAILABLE BALANCE");
        fontManager.applyHeading(availableLabel, 11);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(availableLabel, gbc);

        // balance amt
        balanceLabel = new JLabel();
        fontManager.applyHeading(balanceLabel, 10);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(balanceLabel, gbc);

        Runnable cashInAction = () -> {
            amountPanel.setVisible(true);
            DigitalWallet.this.revalidate();
            DigitalWallet.this.repaint();
        };

        cashInButton = new buttonCreator("Cash In", "Button.font", cashInAction);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel.add(cashInButton, gbc);

        return panel;
    }

    private roundedPanel createAmountPanel() {
        roundedPanel panel = new roundedPanel(15);
        panel.setBackgroundColorKey("Sidebar.hoverBackground");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        // enter amt label
        amountTitle = new JLabel("ENTER AMOUNT");
        fontManager.applyHeading(amountTitle, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(amountTitle, gbc);

        // amt txt field
        amountField = new JTextField();
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 24));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        amountField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = amountField.getText();

                // allow backspace and delete
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                    return;
                }

                // only allow digits
                if (!Character.isDigit(c)) {
                    // exception: allow one single decimal point
                    if (c == '.' && !text.contains(".")) {
                        return; // allow it
                    }
                    e.consume(); // ignore all other characters
                }
            }
        });
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(amountField, gbc);

        Runnable confirmAction = () -> {
            // --- updated logic ---
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty() || amountText.equals(".")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double amountToAdd = Double.parseDouble(amountText);
                if (amountToAdd <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // call the dao
                if (customerDAO != null && currentCustID != -1) {
                    double newBalance = customerDAO.addWalletBalance(currentCustID, amountToAdd);
                    if (newBalance >= 0) {
                        // success
                        JOptionPane.showMessageDialog(this, "Successfully added " + amountToAdd + " to your wallet.", "Top Up Successful", JOptionPane.INFORMATION_MESSAGE);
                        loadBalance(); // refresh the balance label
                        amountField.setText(""); // clear the field
                        amountPanel.setVisible(false); // hide the panel
                    } else {
                        // dao returned -1.0
                        JOptionPane.showMessageDialog(this, "Failed to update balance in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter numbers only.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        };

        buttonCreator confirmButton = new buttonCreator("Confirm", "Button.font", confirmAction);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(confirmButton, gbc);

        return panel;
    }

    // refresh UI
    @Override
    public void updateUI() {
        super.updateUI();
        if (frame != null) {
            setBackground(UIManager.getColor("Panel.background"));
            if (backLabel != null) {
                fontManager.applyHeading(backLabel, 8);
            }
            if (titleLabel != null) {
                fontManager.applyHeading(titleLabel, 5);
            }
            if (balancePanel != null) {
                balancePanel.updateUI();
            }
            if (availableLabel != null) {
                fontManager.applyHeading(availableLabel, 11);
            }
            if (balanceLabel != null) {
                fontManager.applyHeading(balanceLabel, 10);
            }
            if (cashInButton != null) {
                cashInButton.updateUI();
            }
            if (amountPanel != null) {
                amountPanel.updateUI();
            }
            if (amountTitle != null) {
                fontManager.applyHeading(amountTitle, 12);
            }
            if (amountField != null) {
                amountField.setBackground(UIManager.getColor("TextField.background"));
                amountField.setForeground(UIManager.getColor("TextField.foreground"));
                Color borderColor = UIManager.getColor("Component.borderColor");
                if (borderColor == null) borderColor = Color.LIGHT_GRAY;
                amountField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            if (confirmButton != null) {
                confirmButton.updateUI();
            }
        }
    }
}