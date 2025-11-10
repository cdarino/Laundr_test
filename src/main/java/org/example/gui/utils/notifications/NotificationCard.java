package org.example.gui.utils.notifications;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.fonts.fontManager;
import org.example.models.NotificationData;

import javax.swing.*;
import java.awt.*;

/**
 * a simple card to display one notification.
 */
public class NotificationCard extends JPanel {

    private JLabel messageLabel;
    private JLabel dateLabel;
    private JButton markAsReadButton;

    public NotificationCard(NotificationData data, Runnable markAsReadAction) {
        setLayout(new BorderLayout(10, 5));
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 2, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // keep height reasonable

        // message (force wrapping with fixed width)
        messageLabel = new JLabel("<html><body style='width:100px;'>" + data.getMessage() + "</body></html>");
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        fontManager.applyHeading(messageLabel, 8);

        add(messageLabel, BorderLayout.CENTER);

        // date label (below message)
        dateLabel = new JLabel(data.getCreatedAt());
        fontManager.applyHeading(dateLabel, 8);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(dateLabel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // "mark as read" button
        markAsReadButton = new JButton(iconCreator.getIcon("Icons/read.svg", 12, 12));
        markAsReadButton.setToolTipText("Mark as read");
        markAsReadButton.setOpaque(false);
        markAsReadButton.setContentAreaFilled(false);
        markAsReadButton.setBorderPainted(false);
        markAsReadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        markAsReadButton.addActionListener(e -> {
            if (markAsReadAction != null) {
                markAsReadAction.run();
            }
        });
        add(markAsReadButton, BorderLayout.EAST);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (messageLabel != null) {
            setBackground(UIManager.getColor("Menu.background"));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 2, 0, Color.WHITE),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            fontManager.applyHeading(messageLabel, 8);
            fontManager.applyHeading(dateLabel, 8);
        }
    }
}
