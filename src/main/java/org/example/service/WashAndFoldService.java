package org.example.service;

public class WashAndFoldService implements Service {
    @Override
    public String getName() { return "Wash & Fold"; }

    @Override
    public double basePrice() { return 50.0; }

    @Override
    public String description() { return "Standard wash, dry, fold"; }
}