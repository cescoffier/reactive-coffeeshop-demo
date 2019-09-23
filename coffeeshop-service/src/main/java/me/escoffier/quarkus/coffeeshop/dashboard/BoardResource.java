package me.escoffier.quarkus.coffeeshop.dashboard;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Stream;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

@Path("/queue")
public class BoardResource {

    @Inject
    @Stream("beverages")
    Publisher<String> queue;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> getQueue() {
        return Flowable.merge(queue,
                // Trick OpenShift router, resetting idle connections
                Flowable.interval(10, TimeUnit.SECONDS).map(x -> "{}"));
    }

}
