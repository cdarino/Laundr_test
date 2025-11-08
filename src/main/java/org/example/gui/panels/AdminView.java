package org.example.gui.panels;

import org.example.database.AdminDAO;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.adminHeaderCreator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public class AdminView extends JPanel {
    private Mainframe frame;
    private AdminDAO adminDAO;

    // Customer Tab
    private JTable customerTable;
    private JTable ordersTable;
    private DefaultTableModel customerTableModel;
    private DefaultTableModel ordersTableModel;

    // Laundromat Tab
    private JTable laundromatsTable;
    private DefaultTableModel laundromatsTableModel;
    private JTextField laundromatNameField, laundromatAddressField, laundromatImageField, laundromatDistanceField, laundromatEstTimeField, laundromatHighlightsField;
    private JButton addLaundromatButton, updateLaundromatButton, clearLaundromatButton;
    private int selectedLaundromatId = -1; // To store the ID of the selected row

    public AdminView(Mainframe frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // 1. Add the Admin Header
        adminHeaderCreator header = new adminHeaderCreator(frame);
        add(header, BorderLayout.NORTH);

        // 2. Initialize the AdminDAO
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                adminDAO = new AdminDAO(conn);
            } else {
                add(new JLabel("Error: Could not establish database connection."), BorderLayout.CENTER);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            add(new JLabel("Error: " + e.getMessage()), BorderLayout.CENTER);
            return;
        }

        // 3. Create the JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customer Management", createCustomerPanel());
        tabbedPane.addTab("Laundromat Management", createLaundromatPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 4. Load initial data
        refreshCustomerTable();
        refreshLaundromatsTable();
    }

    /**
     * Creates the Customer Management tab.
     */
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Customer Table ---
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Username", "Phone", "Address", "Email"}, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener to customer table
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get customer ID from the first column
                    int custId = (int) customerTableModel.getValueAt(selectedRow, 0);
                    refreshOrdersTable(custId);
                }
            }
        });

        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerScrollPane.setBorder(BorderFactory.createTitledBorder("Customers"));
        panel.add(customerScrollPane);

        // --- Orders Table ---
        ordersTableModel = new DefaultTableModel(new String[]{"Order ID", "Laundromat ID", "Date", "Status", "Total"}, 0);
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBorder(BorderFactory.createTitledBorder("Customer's Orders"));
        panel.add(ordersScrollPane);

        return panel;
    }

    private JPanel createLaundromatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Laundromat Table ---
        laundromatsTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Address", "Rating", "Image", "Distance", "Est. Time", "Highlights"}, 0);
        laundromatsTable = new JTable(laundromatsTableModel);
        laundromatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        laundromatsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = laundromatsTable.getSelectedRow();
                if (selectedRow != -1) {
                    TableModel model = laundromatsTable.getModel();
                    selectedLaundromatId = (int) model.getValueAt(selectedRow, 0); // Store the ID
                    laundromatNameField.setText(model.getValueAt(selectedRow, 1).toString());
                    laundromatAddressField.setText(model.getValueAt(selectedRow, 2).toString());
                    laundromatImageField.setText(model.getValueAt(selectedRow, 4).toString());
                    laundromatDistanceField.setText(model.getValueAt(selectedRow, 5).toString());
                    laundromatEstTimeField.setText(model.getValueAt(selectedRow, 6).toString());
                    laundromatHighlightsField.setText(model.getValueAt(selectedRow, 7).toString());

                    updateLaundromatButton.setEnabled(true); // Enable update
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(laundromatsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Laundromats"));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // --- Form for Adding/Updating Laundromats ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Laundromat"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatNameField = new JTextField(20);
        formPanel.add(laundromatNameField, gbc);

        // Row 1: Address
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatAddressField = new JTextField(20);
        formPanel.add(laundromatAddressField, gbc);

        // Row 2: Image Path
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Image Path:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatImageField = new JTextField(20);
        laundromatImageField.setText("Pictures/default.png"); // Example default
        formPanel.add(laundromatImageField, gbc);

        // Row 3: Distance
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Distance:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatDistanceField = new JTextField(20);
        laundromatDistanceField.setText("N/A"); // Example default
        formPanel.add(laundromatDistanceField, gbc);

        // Row 4: Est. Time
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Est. Time:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatEstTimeField = new JTextField(20);
        laundromatEstTimeField.setText("N/A"); // Example default
        formPanel.add(laundromatEstTimeField, gbc);

        // Row 5: Highlights
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        // --- UPDATED: Added a more descriptive label ---
        formPanel.add(new JLabel("Highlights (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        laundromatHighlightsField = new JTextField(20);
        laundromatHighlightsField.setText("Standard Wash & Fold"); // Example default
        formPanel.add(laundromatHighlightsField, gbc);

        // Row 6: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addLaundromatButton = new JButton("Add New");
        addLaundromatButton.addActionListener(e -> addLaundromat());
        buttonPanel.add(addLaundromatButton);

        updateLaundromatButton = new JButton("Update Selected");
        updateLaundromatButton.addActionListener(e -> updateLaundromat());
        updateLaundromatButton.setEnabled(false); // Disabled until a row is clicked
        buttonPanel.add(updateLaundromatButton);

        clearLaundromatButton = new JButton("Clear Form");
        clearLaundromatButton.addActionListener(e -> clearLaundromatForm());
        buttonPanel.add(clearLaundromatButton);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Refreshes the customer table with data from the DB.
     */
    private void refreshCustomerTable() {
        try {
            Vector<Vector<Object>> data = adminDAO.getAllCustomers();
            customerTableModel.setRowCount(0); // Clear existing data
            for (Vector<Object> row : data) {
                customerTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the orders table based on the selected customer.
     */
    private void refreshOrdersTable(int custId) {
        try {
            Vector<Vector<Object>> data = adminDAO.getOrdersByCustomerId(custId);
            ordersTableModel.setRowCount(0); // Clear existing data
            for (Vector<Object> row : data) {
                ordersTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the laundromats table with data from the DB.
     */
    private void refreshLaundromatsTable() {
        try {
            Vector<Vector<Object>> data = adminDAO.getAllLaundromats();
            laundromatsTableModel.setRowCount(0); // Clear existing data
            for (Vector<Object> row : data) {
                laundromatsTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading laundromats: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Add New" button click.
     */
    private void addLaundromat() {
        try {
            boolean success = adminDAO.addLaundromat(
                    laundromatNameField.getText(),
                    laundromatAddressField.getText(),
                    laundromatImageField.getText(),
                    laundromatDistanceField.getText(),
                    laundromatEstTimeField.getText(),
                    laundromatHighlightsField.getText()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Laundromat added successfully!");
                refreshLaundromatsTable();
                clearLaundromatForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add laundromat.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding laundromat: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Update Selected" button click.
     */
    private void updateLaundromat() {
        if (selectedLaundromatId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a laundromat from the table to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = adminDAO.updateLaundromat(
                    selectedLaundromatId,
                    laundromatNameField.getText(),
                    laundromatAddressField.getText(),
                    laundromatImageField.getText(),
                    laundromatDistanceField.getText(),
                    laundromatEstTimeField.getText(),
                    laundromatHighlightsField.getText()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Laundromat updated successfully!");
                refreshLaundromatsTable();
                clearLaundromatForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update laundromat.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating laundromat: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Clear Form" button click.
     */
    private void clearLaundromatForm() {
        laundromatNameField.setText("");
        laundromatAddressField.setText("");
        laundromatImageField.setText("Pictures/default.png");
        laundromatDistanceField.setText("N/A");
        laundromatEstTimeField.setText("N/A");
        laundromatHighlightsField.setText("Standard Wash & Fold");

        selectedLaundromatId = -1; // Clear selection ID
        laundromatsTable.clearSelection(); // Clear table selection
        updateLaundromatButton.setEnabled(false); // Disable update button
    }
}