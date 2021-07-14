package me.escoffier.quarkus.coffeeshop.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
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

    @Inject
    ObjectMapper mapper;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> getQueue() {
        return Multi.createBy().merging()
                .streams(
                        queue.map(this::toJson),
                        getPingStream()
                );
    }

    Multi<String> getPingStream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10))
                .onItem().transform(x -> "{}");
    }

    private String toJson(Beverage b) {
        try {
            return mapper.writeValueAsString(b);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}
