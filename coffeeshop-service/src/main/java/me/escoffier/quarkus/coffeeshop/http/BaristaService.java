package me.escoffier.quarkus.coffeeshop.http;

import io.smallrye.mutiny.Uni;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.temporal.ChronoUnit;

@Path("/barista")
@RegisterRestClient(configKey = "barista-http")
public interface BaristaService {

    @POST
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "fallback")
    Beverage order(Order order);


    default Beverage fallback(Order order) {
        return new Beverage(order.product(), order.customer(), null, order.orderId(), Beverage.State.FAILED);
    }
}
