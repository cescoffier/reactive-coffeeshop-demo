package me.escoffier.quarkus.coffeeshop;

import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.messaging.KafkaBaristas;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CoffeeShopResource {

    @Inject
    @RestClient
    BaristaService barista;


    @POST
    @Path("/http")
    public Beverage http(Order order) {
        return barista.order(order.setOrderId(UUID.randomUUID().toString()));
    }

    @Inject
    KafkaBaristas baristas;

    @POST
    @Path("/messaging")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<Response> messaging(Order order) {
        System.out.println("Ordering a " + order.getProduct());
        order.setOrderId(UUID.randomUUID().toString());
        return baristas.order(order).thenApply(x -> Response.accepted(order.getOrderId()).build());
    }

}
