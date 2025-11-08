package org.example.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import org.example.gui.panels.AdminView;
import org.example.gui.panels.Landing;
import org.example.gui.panels.Login;
import org.example.gui.panels.Register;
import org.example.gui.utils.fonts.fontLoader;

import javax.swing.*;
import java.awt.*;

public class Mainframe extends JFrame {
    public static boolean dark = false;
    private CardLayout cardLayout;
    public JPanel mainPanel;

    private String currentUser;

    public Mainframe() {
        fontLoader.loadFonts();
        loadTheme();

        initializeFrame();
        setupUI();
    }

    private void loadTheme() {
        try {
            FlatLaf.registerCustomDefaultsSource("Themes");
            if (dark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeFrame() {
        setTitle("Laundr");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = screenSize.width * 9/10;
        int height = screenSize.height * 8/9;

        this.setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // add cards
        mainPanel.add(new Login(this), "LOGIN");
        mainPanel.add(new Register(this), "REGISTER");
        mainPanel.add(new Landing(this), "LANDING");
        mainPanel.add(new AdminView(this), "ADMIN");

        setContentPane(mainPanel);
        showCard("LOGIN");
    }

    public void showCard(String name) {
        // new landing that reads frame.getCurrentUser
        if (name.equals("LANDING")) {
            mainPanel.add(new Landing(this), "LANDING");
        }
        cardLayout.show(mainPanel, name);
    }

    public void toggleTheme() {
        // manage themes
        dark = !dark;

        // Take a snapshot of the current UI, switch LAF, then animate/hide the snapshot.
        FlatAnimatedLafChange.showSnapshot();
        try {
            FlatLaf.registerCustomDefaultsSource("Themes");
            UIManager.setLookAndFeel(dark ? new FlatDarkLaf() : new FlatLightLaf());

            // Re-apply your font and color defaults for the new LAF
            fontLoader.loadFonts();

            UIManager.put("defaultFont", new Font("Lato Regular", Font.PLAIN, 16));
            UIManager.put("Title.font", new Font("Fredoka Bold", Font.BOLD, 36));
            UIManager.put("Button.font", new Font("Lato Bold", Font.BOLD, 15));

            UIManager.put("headerColor", dark ? new Color(0xF8FBFD) : new Color(0x31A4E5));
            UIManager.put("Button.foreground", new Color(0xF8FBFD));
            UIManager.put("Label.foreground", dark ? new Color(0xF8FBFD) : new Color(0x273755));

            UIManager.put("Heading.foreground", new Color (0xF8FBFD));
            UIManager.put("dashboardUser.foreground", new Color(0xDAEC73));

            // Update all open windows in one go (avoids visible partial refresh)
            FlatLaf.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
    }

    public boolean isDarkMode() {
        return dark;
    }

    public void setCurrentUser(String user) {
        this.currentUser = user;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        setCurrentUser(null);
        showCard("LOGIN");
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

}
