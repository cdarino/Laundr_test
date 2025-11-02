package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.creators.buttonCreator; // <-- CHANGED
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DigitalWallet extends JPanel {

    private Mainframe frame;
    private Landing landing;

    private JLabel balanceLabel;
    private JTextField amountField;
    private JPanel amountPanel;

    public DigitalWallet(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;
        initComponents();
    }

    private void initComponents() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setBackground(UIManager.getColor("Panel.background"));

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
        add(backLabel);
        add(Box.createVerticalStrut(20));

        JLabel titleLabel = new JLabel("Digital Wallet");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        amountPanel = createAmountPanel();
        JPanel balancePanel = createBalancePanel();

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

    private JPanel createBalancePanel() {
        roundedPanel panel = new roundedPanel(15);
        panel.setBackgroundColorKey("Menu.background");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        // available balance lbl
        JLabel availableLabel = new JLabel("AVAILABLE BALANCE");
        fontManager.applyHeading(availableLabel, 11);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(availableLabel, gbc);

        // balance amt
        balanceLabel = new JLabel("P 500.00");
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

        buttonCreator cashInButton = new buttonCreator("Cash In", "Button.font", cashInAction);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel.add(cashInButton, gbc);

        return panel;
    }

    private JPanel createAmountPanel() {
        roundedPanel panel = new roundedPanel(15);
        panel.setBackgroundColorKey("Sidebar.hoverBackground");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        // enter amt label
        JLabel amountTitle = new JLabel("ENTER AMOUNT");
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
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(amountField, gbc);

        Runnable confirmAction = () -> {
            // insert confirmation logic
            System.out.println("Confirming amount: " + amountField.getText());
            // hide panel after confirm
            amountPanel.setVisible(false);
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
            SwingUtilities.invokeLater(this::initComponents);
        }
    }
}