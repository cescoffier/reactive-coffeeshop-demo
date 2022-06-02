package me.escoffier.quarkus.coffeeshop;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

import static me.escoffier.quarkus.coffeeshop.Names.pickAName;

@ApplicationScoped
public class KafkaBarista {

    @Inject Logger logger;

    private final String barista = pickAName();
    private final Random random = new Random();

    //TODO orders -> prepare (blocking) -> queue

    @Incoming("orders")
    @Outgoing("queue")
    @Blocking
    public Beverage process(Order order) {
        return prepare(order);
    }

    Beverage prepare(Order order) {
        int delay = getPreparationTime();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Beverage beverage = Beverage.from(order, barista, Beverage.State.READY);
        logger.infof("Order %s for %s is ready: %s", order.product(), order.customer(), beverage);
        return beverage;
    }

    int getPreparationTime() {
        return random.nextInt(5) * 1000;
    }

}
