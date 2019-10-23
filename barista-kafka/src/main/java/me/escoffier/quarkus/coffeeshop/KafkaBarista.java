package me.escoffier.quarkus.coffeeshop;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ApplicationScoped
public class KafkaBarista {

    @ConfigProperty(name = "barista.name")
    String name;

    private Jsonb jsonb = JsonbBuilder.create();
    private Random random = new Random();

    @Incoming("orders")
    @Outgoing("queue")
    public CompletionStage<String> prepare(String message) {
        Order order = jsonb.fromJson(message, Order.class);
        System.out.println("Barista " + name + " is going to prepare a " + order.getProduct());
        return makeIt(order)
                .thenApply(beverage -> PreparationState.ready(order, beverage));
    }

    private CompletionStage<Beverage> makeIt(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Preparing a " + order.getProduct());
            prepare();
            return new Beverage(order, name);
        }, executor);
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    private void prepare() {
        try {
            Thread.sleep(random.nextInt(5000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
