package me.escoffier.quarkus.coffeeshop.http;

import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.CompletionStage;

@Path("/barista")
@RegisterRestClient
public interface BaristaService {

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Beverage order(Order order);

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    CompletionStage<Beverage> orderAsync(Order order);


}
