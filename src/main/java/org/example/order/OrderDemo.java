package org.example.order;

import org.example.service.Service;
import org.example.service.ServiceFactory;

public class OrderDemo {
    public static void main(String[] args) {
        Service s1 = ServiceFactory.create("washandfold");
        Service s2 = ServiceFactory.create("dryclean");

        LaundryOrder order = new LaundryOrder.Builder()
                .pickupAddress("123 Rizal St.")
                .deliveryAddress("456 Mabini Ave.")
                .contact("juza@example.com")
                .addItem(s1, 2)
                .addItem(s2, 1)
                .instructions("Handle delicates separately")
                .build();

        System.out.println(order);
        order.getItems().forEach(it -> System.out.println(" - " + it));
        System.out.printf("Subtotal: %.2f%n", order.subtotal());
    }
}
