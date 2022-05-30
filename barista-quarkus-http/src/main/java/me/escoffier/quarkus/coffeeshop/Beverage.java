package me.escoffier.quarkus.coffeeshop;

public record Beverage(String beverage, String customer, String preparedBy, String orderId) {

    public static Beverage from(Order order, String baristaName) {
        return new Beverage(
                order.product(),
                order.customer(),
                baristaName,
                order.orderId());
    }
}
