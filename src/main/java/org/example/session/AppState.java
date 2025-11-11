package org.example.session;

/**
 * Small global state holder for selected laundromat and optional current customer id.
 * Kept intentionally tiny and procedural to minimize refactor.
 */
public final class AppState {
    private AppState() {}

    // Selected laundromat chosen in Laundromats -> LaundromatDetails
    // -1 means "no selection"
    public static int selectedLaundromatID = -1;
    public static String selectedLaundromatName = null;

    // Optional convenience to set both
    public static void setSelectedLaundromat(int id, String name) {
        selectedLaundromatID = id;
        selectedLaundromatName = name;
    }

    public static void clearSelectedLaundromat() {
        selectedLaundromatID = -1;
        selectedLaundromatName = null;
    }
}