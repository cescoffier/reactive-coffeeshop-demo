package me.escoffier.quarkus.coffeeshop.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@RegisterForReflection
public class PreparationState {

    Beverage beverage;

    Order order;

    State state;

    public enum State {
        IN_QUEUE,
        BEING_PREPARED,
        READY;
    }

    private static final Jsonb JSON = JsonbBuilder.create();

    public static String ready(Order order, Beverage beverage) {
        return JSON.toJson(new PreparationState().setBeverage(beverage).setOrder(order).setState(State.READY));
    }

    public static String queued(Order order) {
        return JSON.toJson(new PreparationState().setOrder(order).setState(State.IN_QUEUE));
    }

    public static String underPreparation(Order order) {
        return JSON.toJson(new PreparationState().setOrder(order).setState(State.BEING_PREPARED));
    }


    public Beverage getBeverage() {
        return beverage;
    }

    public PreparationState setBeverage(Beverage beverage) {
        this.beverage = beverage;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public PreparationState setOrder(Order order) {
        this.order = order;
        return this;
    }

    public State getState() {
        return state;
    }

    public PreparationState setState(State state) {
        this.state = state;
        return this;
    }
}
