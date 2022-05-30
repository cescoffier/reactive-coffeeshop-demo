package me.escoffier.quarkus.coffeeshop.model;

public record Beverage(String beverage, String customer, String preparedBy, String orderId, State state) {


    public static Beverage queued(Order order) {
        return new Beverage(order.product(), order.customer(), null, order.orderId(), State.IN_QUEUE);
    }

    public Beverage ready() {
        if (state != State.FAILED) {
            return new Beverage(beverage, customer, preparedBy, orderId, State.READY);
        }
        return this;
    }

    public enum State {
        IN_QUEUE,
        BEING_PREPARED,
        READY,
        FAILED
    }

}

