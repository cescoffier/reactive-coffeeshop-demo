package me.escoffier.quarkus.coffeeshop.dashboard;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Channel;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

@Path("/queue")
public class BoardResource {

    @Inject
    @Channel("beverages")
    Flowable<Beverage> queue;

    private Jsonb json = JsonbBuilder.create();

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> getQueue() {
        return Flowable.merge(
                queue.map(b -> json.toJson(b)),
                // Trick OpenShift router, resetting idle connections
                Flowable.interval(10, TimeUnit.SECONDS).map(x -> "{}"));
    }

}
