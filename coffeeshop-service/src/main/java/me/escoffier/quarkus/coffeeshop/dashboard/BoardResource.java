package me.escoffier.quarkus.coffeeshop.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/queue")
public class BoardResource {

    @Channel("beverages")
    Multi<Beverage> queue;

    @Inject
    ObjectMapper mapper;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> getQueue() {
        // Why merging?
        // It's because the Cloud (OpenShift, AWS...) tends to cut the connection after 10s of inactivity.
        // They do so to recycle the connection. However, in our case it's very annoying.
        // So, here is a trick, every 10 seconds, we inject an "empty" json object `{}`, which will be managed
        // by the Web UI as a noop. Thus, the connection is not cut as there is still some traffic.
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
