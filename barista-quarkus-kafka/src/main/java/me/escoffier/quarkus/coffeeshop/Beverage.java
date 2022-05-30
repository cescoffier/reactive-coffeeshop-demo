package me.escoffier.quarkus.coffeeshop;

public record Beverage(String beverage, String customer, String preparedBy, String orderId, State state) {


    public enum State {
        IN_QUEUE,
        BEING_PREPARED,
        READY;
    }

    public static Beverage from(Order order, String baristaName, State state) {
        return new Beverage(order.product(),
                order.customer(),
                baristaName,
                order.orderId(),
                state);
    }

}
