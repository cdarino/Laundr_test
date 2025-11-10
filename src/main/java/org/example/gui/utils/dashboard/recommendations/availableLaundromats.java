package org.example.gui.utils.dashboard.recommendations;

import org.example.database.DBConnect;
import org.example.database.LaundromatDAO;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Vector;

public class availableLaundromats extends roundedPanel {
    private JLabel laundromats;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private LaundromatDAO laundromatDAO;
    private final Mainframe frame;

    private JScrollPane scrollPane;
    public JLabel laundromatsLabel;

    public availableLaundromats(Mainframe frame) {
        super(16);
        this.frame = frame;

        // Initialize DAO
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                this.laundromatDAO = new LaundromatDAO(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(new roundedBorder(16, UIManager.getColor("listBorder"), 2));

        laundromatsLabel = new JLabel("Available Laundromats");
        laundromatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fontManager.applyHeading(laundromatsLabel, 4);
        laundromatsLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        add(laundromatsLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);

        list.setOpaque(true);
        list.setBackground(UIManager.getColor("background"));

        list.setForeground(UIManager.getColor("Label.foreground"));
        list.setFont(fontManager.h7());
        list.setFixedCellHeight(40);
        list.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(UIManager.getColor("background"));

        add(scrollPane, BorderLayout.CENTER);

        loadLaundromats();
    }

    /**
     * Fetches laundromat names from the DAO and populates the list.
     */
    private void loadLaundromats() {
        if (laundromatDAO == null) {
            listModel.addElement("Error: DB Connection failed.");
            return;
        }

        try {
            Vector<String> names = laundromatDAO.getAllLaundromatNames();
            listModel.clear();
            if (names.isEmpty()) {
                listModel.addElement("No laundromats found.");
            } else {
                for (String name : names) {
                    listModel.addElement("• " + name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listModel.addElement("Error loading data.");
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();

        setBackground(UIManager.getColor("Menu.background"));
        setBorder(new roundedBorder(16, UIManager.getColor("listBorder"), 2));

        if (list != null) {
            list.setBackground(UIManager.getColor("background"));
            list.setForeground(UIManager.getColor("Label.foreground"));

            if (scrollPane != null) {
                scrollPane.getViewport().setBackground(UIManager.getColor("background"));
            }
        }
    }
}