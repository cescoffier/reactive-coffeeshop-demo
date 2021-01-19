package me.escoffier.quarkus.coffeeshop;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Beverage {

    public String beverage;
    public String customer;
    public String preparedBy;
    public String orderId;
    public State preparationState;

    public enum State {
        IN_QUEUE,
        BEING_PREPARED,
        READY;
    }

    public Beverage() {
        // Used by JSON-B
    }

    public Beverage(Order order, String baristaName, State state) {
        this.beverage = order.getProduct();
        this.customer = order.getName();
        this.orderId = order.getOrderId();
        this.preparedBy = baristaName;
        this.preparationState = state;
    }

}
