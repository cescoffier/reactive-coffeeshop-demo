package me.escoffier.quarkus.coffeeshop;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.escoffier.quarkus.coffeeshop.Names.pickAName;

@Path("/barista")
public class BaristaResource {

    @Inject
    Logger logger;

    private final ExecutorService queue = Executors.newSingleThreadExecutor();
    private final String barista = pickAName();
    private final Random random = new Random();

    @POST
    public Uni<Beverage> process(Order order) {
        return Uni.createFrom().item(() -> {
            Beverage coffee = prepare(order);
            logger.infof("Order %s for %s is ready", order.product(), order.customer());
            return coffee;
        }).runSubscriptionOn(queue);
    }

    Beverage prepare(Order order) {
        int delay = getPreparationTime();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Beverage.from(order, barista);
    }


    int getPreparationTime() {
        return random.nextInt(5) * 1000;
    }

}
