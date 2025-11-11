package org.example.gui.laundromats;

/**
 * Simple DTO for laundromat data.
 */
public class LaundromatData {
    public String name;
    public String address;
    public String distance;
    public String deliveryPeriod;
    public int stars;
    public String description;
    public String imagePath;
    public String highlights;    // NEW: highlights text stored in DB (semicolon/comma separated)
    public double pricePerLoad;  // NEW: price per load

    /**
     * Full constructor.
     */
    public LaundromatData(String name,
                          String address,
                          String distance,
                          String deliveryPeriod,
                          int stars,
                          String description,
                          String imagePath,
                          String highlights,
                          double pricePerLoad) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.deliveryPeriod = deliveryPeriod;
        this.stars = stars;
        this.description = description;
        this.imagePath = imagePath;
        this.highlights = highlights;
        this.pricePerLoad = pricePerLoad;
    }

    /**
     * Convenience constructor used by the samples (no stars/description/price/highlights).
     */
    public LaundromatData(String name,
                          String address,
                          String distance,
                          String deliveryPeriod,
                          String imagePath) {
        this(name, address, distance, deliveryPeriod, 0, "", imagePath, null, 0.0);
    }
}