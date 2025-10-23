package org.example.service;

public class ServiceFactoryDemo {
    public static void main(String[] args) {
        Service s1 = ServiceFactory.create("washandfold");
        Service s2 = ServiceFactory.create("dryclean");
        Service s3 = ServiceFactory.create("pressonly");

        System.out.println(s1.getName() + " -> " + s1.description() + " : " + s1.basePrice());
        System.out.println(s2.getName() + " -> " + s2.description() + " : " + s2.basePrice());
        System.out.println(s3.getName() + " -> " + s3.description() + " : " + s3.basePrice());
    }
}
