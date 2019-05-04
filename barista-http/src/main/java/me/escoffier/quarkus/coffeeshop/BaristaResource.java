package me.escoffier.quarkus.coffeeshop;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/barista")
@Produces(MediaType.APPLICATION_JSON)
public class BaristaResource {

    private ExecutorService barista = Executors.newSingleThreadExecutor();

    private Random random = new Random();

    @ConfigProperty(name = "barista.name")
    String name;

    @POST
    public CompletionStage<Beverage> prepare(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            Beverage beverage = makeIt(order);
            System.out.println("Order " + order.getProduct() + " for " + order.getName() + " is ready");
            return beverage;
        }, barista);
    }

    private Beverage makeIt(Order order) {
        int delay = random.nextInt(5) * 1000;

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // Ignored
            Thread.currentThread().interrupt();
        }
        return new Beverage(order, name);

    }


}
