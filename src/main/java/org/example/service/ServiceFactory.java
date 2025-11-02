package org.example.service;

public final class ServiceFactory {
    private ServiceFactory() {}

    /**
     * Create service by a simple key. Keys are case-insensitive.
     * Example keys: "washandfold", "dryclean", "pressonly"
     */
    public static Service create(String key) {
        if (key == null) throw new IllegalArgumentException("key is null");
        switch (key.trim().toLowerCase()) {
            case "washandfold":
            case "wash-and-fold":
            case "wash":
                return new WashAndFoldService();
            case "dryclean":
            case "dry-clean":
            case "dry":
                return new DryCleanService();
            case "pressonly":
            case "press-only":
            case "press":
                return new PressOnlyService();
            default:
                throw new IllegalArgumentException("Unknown service key: " + key);
        }
    }
}