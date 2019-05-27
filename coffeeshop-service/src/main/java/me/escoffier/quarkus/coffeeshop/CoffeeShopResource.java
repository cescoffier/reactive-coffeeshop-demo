package me.escoffier.quarkus.coffeeshop;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import me.escoffier.quarkus.coffeeshop.model.PreparationState;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
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
    Jsonb jsonb;

    @Inject
    @RestClient
    BaristaService barista;


    @POST
    @Path("/http")
    public Beverage http(Order order) {
        return barista.order(order.setOrderId(UUID.randomUUID().toString()));
    }

    @POST
    @Path("/async")
    public CompletionStage<Beverage> async(Order order) {
        return barista.orderAsync(order.setOrderId(UUID.randomUUID().toString()));
    }

    @Inject @Stream("orders")
    Emitter<String> orders;

    @Inject @Stream("queue")
    Emitter<String> queue;

    @POST
    @Path("/messaging")
    public Order messaging(Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        queue.send(PreparationState.queued(order));
        orders.send(jsonb.toJson(order));
        return order;
    }


}
