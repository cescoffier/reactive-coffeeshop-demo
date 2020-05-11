package me.escoffier.quarkus.coffeeshop.dashboard;

import io.smallrye.mutiny.Multi;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/queue")
public class BoardResource {

    @Inject
    @Channel("beverages")
    Multi<Beverage> queue;

    private Jsonb json = JsonbBuilder.create();

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> getQueue() {
        return Multi.createBy().merging()
                .streams(
                        queue.map(b -> json.toJson(b)),
                        getPingStream()
                );
    }

    Multi<String> getPingStream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10))
                .onItem().apply(x -> "{}");
    }

}
