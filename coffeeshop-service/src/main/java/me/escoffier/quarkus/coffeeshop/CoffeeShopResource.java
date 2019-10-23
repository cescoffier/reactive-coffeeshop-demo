package me.escoffier.quarkus.coffeeshop;

import io.smallrye.reactive.messaging.annotations.Channel;
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

    // Emitter on channel orders
    @Inject @Channel("orders") Emitter<String> orders;
    // Emitter on channel queue
    @Inject @Channel("queue") Emitter<String> queue;

    @Path("/messaging")
    @POST
    public Order messaging(Order order) {
        Order processed = process(order);
        queue.send(getPreparationState(processed));
        orders.send(toJson(processed));
        return processed;
    }

    private String toJson(Order processed) {
        return jsonb.toJson(processed);
    }

    private String getPreparationState(Order processed) {
        return PreparationState.queued(processed);
    }

    private Order process(Order order) {
        return order.setOrderId(UUID.randomUUID().toString());
    }


}
