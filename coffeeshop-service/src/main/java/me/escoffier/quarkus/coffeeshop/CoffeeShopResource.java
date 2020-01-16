package me.escoffier.quarkus.coffeeshop;

import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CoffeeShopResource {

    @Inject
    @RestClient
    BaristaService barista;

    @POST
    @Path("/http")
    public Beverage http(Order order) {
        return barista.order(order.setOrderId(getId()));
    }

    @POST
    @Path("/async")
    public CompletionStage<Beverage> async(Order order) {
        return barista.orderAsync(order.setOrderId(getId()));
    }

    @Inject
    @Channel("orders")
    Emitter<Order> orders;

    @Inject
    @Channel("queue")
    Emitter<Beverage> states;

    @POST
    @Path("/messaging")
    public Order messaging(Order order) {
        System.out.println(order.getName() + " / " + order.getProduct());
        order.setOrderId(getId());
        states.send(Beverage.queued(order));
        orders.send(order);
        return order;
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
