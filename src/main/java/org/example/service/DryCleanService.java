package org.example.service;

public class DryCleanService implements Service {
    @Override
    public String getName() { return "Dry Clean"; }

    @Override
    public double basePrice() { return 150.0; }

    @Override
    public String description() { return "Dry cleaning for delicate items"; }
}