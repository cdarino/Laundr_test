package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.database.DBConnect;
import org.example.gui.Mainframe;
import org.example.gui.utils.fonts.fontLoader;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        fontLoader.loadFonts();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new Mainframe().setVisible(true);
        });
    }
}
