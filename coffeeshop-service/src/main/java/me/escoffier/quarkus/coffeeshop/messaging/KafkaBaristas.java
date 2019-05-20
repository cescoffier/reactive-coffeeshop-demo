package me.escoffier.quarkus.coffeeshop.messaging;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import me.escoffier.quarkus.coffeeshop.model.Order;
import me.escoffier.quarkus.coffeeshop.model.PreparationState;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ApplicationScoped
public class KafkaBaristas {

    @Inject
    @Stream("orders")
    Emitter<String> orders;

    @Inject
    @Stream("queue")
    Emitter<String> states;

    @Inject
    Jsonb jsonb;

    private Executor executor = Executors.newSingleThreadExecutor();

    public CompletionStage<Order> order(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            states.send(PreparationState.queued(order));
            orders.send(jsonb.toJson(order));
            return order;
        }, executor);
    }

}
