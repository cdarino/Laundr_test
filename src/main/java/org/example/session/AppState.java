package org.example.session;

/**
 * Simple global app state fallback for cross-panel selections.
 */
public final class AppState {
    private AppState() {}

    // Last selected laundromat name (clicking a laundromat card / showing details sets this)
    public static String selectedLaundromatName = null;

    // Last selected laundromat ID (resolved from DB when details are shown).
    // Prefer this ID when persisting orders.
    public static int selectedLaundromatID = 0;

    // (Other shared state can go here later)
}