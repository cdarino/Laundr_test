package org.example.service;

public class PressOnlyService implements Service {
    @Override
    public String getName() { return "Press Only"; }

    @Override
    public double basePrice() { return 40.0; }

    @Override
    public String description() { return "Ironing / pressing only"; }
}
