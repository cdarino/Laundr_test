package org.example.gui.utils.notifications;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.NotificationDAO;
import org.example.gui.Mainframe;
import org.example.gui.panels.Landing;
import org.example.gui.utils.fonts.fontManager;
import org.example.models.NotificationData;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

/**
 * collapsible panel on the right side for notifications.
 */
public class NotificationsPanel extends JPanel {

    private static final int PANEL_WIDTH = 300; // width of the panel
    private boolean collapsed = true;

    private Mainframe frame;
    private Landing landing;
    private NotificationDAO notificationDAO;
    private CustomerDAO customerDAO;

    private JPanel listContainer;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JLabel emptyLabel;

    private int currentCustID = -1; // cache the user id

    public NotificationsPanel(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;

        // --- initialize daos ---
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                this.notificationDAO = new NotificationDAO(conn);
                this.customerDAO = new CustomerDAO(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, UIManager.getColor("Menu.background")));

        // set the panel's size
        Dimension panelSize = new Dimension(PANEL_WIDTH, Integer.MAX_VALUE);
        setPreferredSize(panelSize);
        setMaximumSize(panelSize);
        setMinimumSize(new Dimension(0, 0)); // allow to collapse to 0

        // 1. title
        titleLabel = new JLabel("Notifications");
        fontManager.applyHeading(titleLabel, 4);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(titleLabel, BorderLayout.NORTH);

        // 2. list container
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(true);
        listContainer.setBackground(UIManager.getColor("Panel.background")); // inner bg

        // 3. scroll pane
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // start collapsed
        setVisible(false);
    }

    /**
     * fetches notifications from the db and populates the list.
     */
    public void loadNotifications() {
        if (notificationDAO == null || customerDAO == null) {
            System.err.println("notificationspanel: daos not initialized.");
            listContainer.removeAll();
            listContainer.add(new JLabel("  error: daos not loaded."));
            return;
        }

        // clear old data
        listContainer.removeAll();

        try {
            String username = frame.getCurrentUser();
            if (username == null) {
                listContainer.add(new JLabel("  please log in."));
                return;
            }

            if (currentCustID == -1) {
                currentCustID = customerDAO.getCustomerId(username);
            }

            if (currentCustID == -1) {
                listContainer.add(new JLabel("  error: could not find user."));
                return;
            }

            // fetch data
            Vector<NotificationData> notifications = notificationDAO.getUnreadNotifications(currentCustID);

            if (notifications.isEmpty()) {
                if (emptyLabel == null) {
                    emptyLabel = new JLabel("  no unread notifications.");
                }
                fontManager.applyHeading(emptyLabel, 8);
                listContainer.add(emptyLabel);
            } else {
                for (NotificationData data : notifications) {
                    NotificationCard card = new NotificationCard(data, () -> markAsRead(data.getNotificationID()));
                    listContainer.add(card);
                    listContainer.add(Box.createVerticalStrut(5));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            listContainer.add(new JLabel("  error loading notifications."));
        }

        // refresh the ui
        listContainer.revalidate();
        listContainer.repaint();
    }

    /**
     * callback method for the notification card to run.
     */
    private void markAsRead(int notificationID) {
        if (notificationDAO == null) return;

        try {
            boolean success = notificationDAO.markAsRead(notificationID);
            if (success) {
                // if successful, just reload the list
                loadNotifications();
            } else {
                System.err.println("failed to mark notification as read.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * toggle collapse (hide/show).
     */
    public void toggle() {
        setCollapsed(!collapsed);
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;

        // if we are opening the panel, refresh the data
        if (!collapsed) {
            loadNotifications();
        } else {
            // if closing, reset the custid cache
            currentCustID = -1;
        }

        setVisible(!collapsed);
        // tell the parent (landing) to re-layout
        landing.revalidate();
        landing.repaint();
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (titleLabel != null) {
            setBackground(UIManager.getColor("Menu.background"));
            fontManager.applyHeading(titleLabel, 4);
        }
        if (listContainer != null) {
            listContainer.setBackground(UIManager.getColor("Panel.background"));
        }
        if (emptyLabel != null) {
            fontManager.applyHeading(emptyLabel, 8);
        }
        setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, UIManager.getColor("Menu.background")));
    }
}