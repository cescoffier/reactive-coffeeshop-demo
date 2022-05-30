package me.escoffier.quarkus.coffeeshop.model;

public record Order(String product, String customer, String orderId) {

    public Order withOrderId(String id) {
        return new Order(product, customer, id);
    }
}