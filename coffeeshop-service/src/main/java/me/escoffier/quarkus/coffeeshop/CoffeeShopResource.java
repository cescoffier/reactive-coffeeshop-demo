package me.escoffier.quarkus.coffeeshop;

import io.smallrye.mutiny.Uni;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.Duration;
import java.util.UUID;

@Path("/")
public class CoffeeShopResource {

    @RestClient
    BaristaService barista;

    @POST
    @Path("/http")
    public Uni<Beverage> http(Order order) {
        return barista.order(order.setOrderId(getId()))
                .onItem().invoke(beverage -> beverage.preparationState = Beverage.State.READY)
                .ifNoItem().after(Duration.ofMillis(1500)).fail()
                .onFailure().recoverWithItem(createFallbackBeverage(order));
    }

    private Beverage createFallbackBeverage(Order order) {
        return new Beverage(order, null, Beverage.State.FAILED);
    }

    // Orders emitter (orders)
    @Channel("orders")
    Emitter<Order> orders;
    // Queue emitter (beverages)
    @Channel("queue")
    Emitter<Beverage> queue;

    @POST
    @Path("/messaging")
    public Order messaging(Order order) {
        order = order.setOrderId(getId());
        queue.send(Beverage.queued(order));
        orders.send(order);
        return order;
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
