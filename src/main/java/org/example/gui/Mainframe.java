package org.example.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import org.example.gui.panels.Landing;
import org.example.gui.panels.Login;
import org.example.gui.panels.Register;
import org.example.gui.utils.fonts.fontLoader;

import javax.swing.*;
import java.awt.*;

public class Mainframe extends JFrame {
    private static boolean dark = false;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

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

        mainPanel.add(new Login(this), "LOGIN");
        mainPanel.add(new Register(this), "REGISTER");
        mainPanel.add(new Landing(this), "LANDING");

        setContentPane(mainPanel);
        showCard("LOGIN");
    }

    public static void showCard(String name) {
        cardLayout.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void toggleTheme() {
        dark = !dark;

        try {
            FlatLaf.registerCustomDefaultsSource("Themes");
            if (dark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }

            fontLoader.loadFonts();

            UIManager.put("defaultFont", new Font("Lato Regular", Font.PLAIN, 16));
            UIManager.put("Title.font", new Font("Fredoka Bold", Font.BOLD, 36));
            UIManager.put("Button.font", new Font("Lato Bold", Font.BOLD, 15));

            UIManager.put("headerColor", dark ? new Color(0xF8FBFD) : new Color(0x31A4E5));
            UIManager.put("Button.foreground", new Color(0xF8FBFD));
            UIManager.put("Label.foreground", dark ? new Color(0xF8FBFD) : new Color(0x273755));

            UIManager.put("Heading.foreground", new Color (0xF8FBFD));
            UIManager.put("dashboardUser.foreground", new Color(0xDAEC73));

            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDarkMode() {
        return dark;
    }

}
