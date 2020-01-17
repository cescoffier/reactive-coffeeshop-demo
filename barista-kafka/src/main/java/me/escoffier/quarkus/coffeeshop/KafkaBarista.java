package me.escoffier.quarkus.coffeeshop;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.escoffier.quarkus.coffeeshop.Names.pickAName;

@ApplicationScoped
public class KafkaBarista {

    private static final Logger LOGGER = LoggerFactory.getLogger("Kafka-Barista");

    private String name = pickAName();

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    @Incoming("orders")
    @Outgoing("queue")
    public CompletionStage<Beverage> process(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            Beverage coffee = prepare(order);
            LOGGER.info("Order {} for {} is ready", order.getProduct(), order.getName());
            return coffee;
        }, queue);
    }

    Beverage prepare(Order order) {
        int delay = getPreparationTime();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new Beverage(order, name, Beverage.State.READY);
    }

    private Random random = new Random();

    int getPreparationTime() {
        return random.nextInt(5) * 1000;
    }

}
