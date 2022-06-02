package me.escoffier.quarkus.coffeeshop;

import io.smallrye.mutiny.Uni;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.Duration;
import java.util.UUID;

@Path("/")
public class CoffeeShopResource {

    @RestClient
    BaristaService barista;

    @Inject
    Logger logger;

    @POST
    @Path("/http")
    public Beverage http(Order order) {
        logger.infof("Received order %s on /http", order);
        Beverage beverage = barista.order(order.withOrderId(getId()));
        return beverage.ready();
    }

    // Orders emitter (orders)
    @Channel("orders") Emitter<Order> orders;
    // Queue emitter (beverages)
    @Channel("queue") Emitter<Beverage> queue;

    @POST
    @Path("/messaging")
    public Order messaging(Order order) {
        logger.infof("Received order %s on /messaging", order);
        order = order.withOrderId(getId());
        orders.send(order);
        queue.send(Beverage.queued(order));
        return order;
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
