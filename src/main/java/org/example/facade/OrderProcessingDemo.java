package org.example.facade;

import org.example.order.LaundryOrder;
import org.example.service.Service;
import org.example.service.ServiceFactory;

public class OrderProcessingDemo {
    public static void main(String[] args) {
        Service s1 = ServiceFactory.create("washandfold");
        Service s2 = ServiceFactory.create("dryclean");
        LaundryOrder order = new LaundryOrder.Builder()
                .pickupAddress("123 Rizal St.")
                .deliveryAddress("456 Mabini Ave.")
                .contact("juza@example.com")
                .addItem(s1, 2)
                .addItem(s2, 1)
                .build();

        OrderProcessingFacade facade = new OrderProcessingFacade();
        boolean placed = facade.placeOrder(order, "card-****-1234");

        System.out.println("Order placed? " + placed);
    }
}