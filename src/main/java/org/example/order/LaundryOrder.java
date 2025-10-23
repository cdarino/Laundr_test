package org.example.order;

import org.example.service.Service;
import java.util.*;

/**
 * LaundryOrder model with Builder pattern.
 * Immutable-ish after build.
 */
public class LaundryOrder {
    private final String id;
    private final String pickupAddress;
    private final String deliveryAddress;
    private final String contact;
    private final List<OrderItem> items;
    private final String instructions;
    private final Date createdAt;

    private LaundryOrder(Builder b) {
        this.id = b.id != null ? b.id : UUID.randomUUID().toString();
        this.pickupAddress = b.pickupAddress;
        this.deliveryAddress = b.deliveryAddress;
        this.contact = b.contact;
        this.items = Collections.unmodifiableList(new ArrayList<>(b.items));
        this.instructions = b.instructions;
        this.createdAt = new Date();
    }

    public String getId() { return id; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getContact() { return contact; }
    public List<OrderItem> getItems() { return items; }
    public String getInstructions() { return instructions; }
    public Date getCreatedAt() { return new Date(createdAt.getTime()); }

    public double subtotal() {
        return items.stream().mapToDouble(OrderItem::total).sum();
    }

    @Override
    public String toString() {
        return String.format("LaundryOrder[%s] %s -> %s | items=%d | subtotal=%.2f",
                id, pickupAddress, deliveryAddress, items.size(), subtotal());
    }

    // Builder
    public static class Builder {
        private String id;
        private String pickupAddress;
        private String deliveryAddress;
        private String contact;
        private final List<OrderItem> items = new ArrayList<>();
        private String instructions = "";

        public Builder id(String id){ this.id = id; return this; }
        public Builder pickupAddress(String a){ this.pickupAddress = a; return this; }
        public Builder deliveryAddress(String a){ this.deliveryAddress = a; return this; }
        public Builder contact(String c){ this.contact = c; return this; }
        public Builder addItem(Service service, int qty){
            if(service == null) throw new IllegalArgumentException("service null");
            if(qty <= 0) throw new IllegalArgumentException("quantity <= 0");
            this.items.add(new OrderItem(service, qty));
            return this;
        }
        public Builder instructions(String ins){ this.instructions = ins; return this; }

        public LaundryOrder build(){
            if(pickupAddress == null || deliveryAddress == null || contact == null || items.isEmpty()){
                throw new IllegalStateException("Missing required fields (pickup/delivery/contact/items).");
            }
            return new LaundryOrder(this);
        }
    }

    // Simple inner value type
    public static class OrderItem {
        private final Service service;
        private final int quantity;
        public OrderItem(Service service, int quantity){
            this.service = Objects.requireNonNull(service);
            this.quantity = quantity;
        }
        public Service getService(){ return service; }
        public int getQuantity(){ return quantity; }
        public double total(){ return service.basePrice() * quantity; }
        @Override
        public String toString(){ return String.format("%s x%d (%.2f each)", service.getName(), quantity, service.basePrice()); }
    }
}
