package org.example.gui.utils.fonts;

import javax.swing.*;
import java.awt.*;

public class fontManager {

    public static Font h1() {
        return UIManager.getFont("Title.font");
    }

    public static Font h2() {
        return h1().deriveFont(26f);
    }

    public static Font h3() {
        return h1().deriveFont(20f);
    }

    public static Font h4() {
        return h1().deriveFont(20f);
    }

    public static Font h5() {
        return h1().deriveFont(28f);
    }

    public static Font h6(){
        return UIManager.getFont("Profile.font");
    }

    public static Font h7() {
        return UIManager.getFont("defaultFont");

    }

    public static Font h8() {
        return h7().deriveFont(12f);
    }

    public static Font h9() {
        return h7().deriveFont(20f);
    }

    public static Font h10() {
        return h7().deriveFont(40f);
    }

    public static Font h11(){
        return h7().deriveFont(12f);
    }

    public static Font h12(){
        return h7().deriveFont(28f);
    }

    public static Color h1Color() {
        return UIManager.getColor("Heading.foreground");
    }

    public static Color h2Color() {
        return UIManager.getColor("dashboardUser.foreground");
    }

    public static Color h3Color() {
        return UIManager.getColor("foreground");

    }

    public static Color h4Color() {
        return UIManager.getColor("Button.foreground");
    }

    public static Color h5Color() {
        return UIManager.getColor("foreground");
    }

    public static Color h6Color(){
        return UIManager.getColor("Profile.foreground");
    }

    public static Color h7Color(){
        return UIManager.getColor("wallet.foreground");
    }

    public static void applyHeading(JLabel label, int level) {
        switch (level) {
            case 1 -> {
                label.setFont(h1());
                label.setForeground(h1Color());
            }
            case 2 -> {
                label.setFont(h2());
                label.setForeground(h2Color());
            }
            case 3 -> {
                label.setFont(h3());
                label.setForeground(h3Color());
            }
            case 4 -> {
                label.setFont(h4());
                label.setForeground(h4Color());
            }
            case 5 -> {
                label.setFont(h5());
                label.setForeground(h5Color());
            }
            case 6 ->{
                label.setFont(h6());
                label.setForeground(h6Color());
            }
            case 7 ->{
                label.setFont(h7());
                label.setForeground(h5Color());
            }
            case 8 ->{
                label.setFont(h8());
                label.setForeground(h5Color());
            }
            case 9 ->{
                label.setFont(h9());
                label.setForeground(h5Color());
            }
            case 10 ->{
                label.setFont(h10());
                label.setForeground(h4Color());
            }
            case 11 ->{
                label.setFont(h11());
                label.setForeground(h4Color());
            }
            case 12 ->{
                label.setFont(h11());
                label.setForeground(h7Color());
            }
            case 13 ->{
                label.setFont(h12());
                label.setForeground(h5Color());
            }
        }
    }
}
