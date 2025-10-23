package org.example.gui.laundromats;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * One service card with icon + label.
 * Now supports onClick callback.
 */
public class ServiceCard extends roundedPanel {
    private boolean selected = false;
    public ServiceCard(String text, String iconFile) {
        this(text, iconFile, null); // delegate to new constructor
    }
    public ServiceCard(String text, String iconFile, ActionListener onClick) {
        super(20);
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Sidebarbtn.background"));
        setPreferredSize(new Dimension(120, 100));

        JLabel icon = new JLabel(iconCreator.getIcon(iconFile, 48, 48), SwingConstants.CENTER);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        add(icon, BorderLayout.CENTER);

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        add(lbl, BorderLayout.SOUTH);

        // mouse click -> forward to callback, toggle visual state
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                updateSelectionUI();
                if (onClick != null) {
                    onClick.actionPerformed(new ActionEvent(ServiceCard.this, ActionEvent.ACTION_PERFORMED, text));
                }
            }
        });

        // keyboard accessibility
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dispatchEvent(new MouseEvent(ServiceCard.this, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 1, 1, 1, false));
                }
            }
        });

        updateSelectionUI();
    }

    private void updateSelectionUI() {
        if (selected) {
            setBorder(javax.swing.BorderFactory.createLineBorder(Color.GREEN.darker(), 2));
        } else {
            setBorder(null);
        }
        repaint();
    }
}
