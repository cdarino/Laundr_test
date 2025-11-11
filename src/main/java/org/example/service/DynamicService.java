package org.example.service;

public class DynamicService implements Service {
    private final String name;
    private final String description;
    private final double price;
    private final String iconPath;

    public DynamicService(String name, String description, double price, String iconPath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.iconPath = iconPath;
    }

    @Override
    public String getName() { return name; }

    @Override
    public double basePrice() { return price; }

    @Override
    public String description() { return description; }

    @Override
    public String getIconPath() { return iconPath; }
}
