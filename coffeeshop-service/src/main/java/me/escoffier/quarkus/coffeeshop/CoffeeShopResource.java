package me.escoffier.quarkus.coffeeshop;

import io.smallrye.mutiny.Uni;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CoffeeShopResource {

    @Autowired
    @RestClient
    BaristaService barista;

    @PostMapping("/http")
    public Uni<Beverage> http(Order order) {
        return barista.order(order.setOrderId(getId()))
                .onItem().invoke(beverage -> beverage.setPreparationState(Beverage.State.READY))
                .ifNoItem().after(Duration.ofMillis(1500)).recoverWithItem(createFallbackBeverage(order))
                .onFailure().recoverWithItem(createFallbackBeverage(order));
    }

    private Beverage createFallbackBeverage(Order order) {
        return new Beverage(order, null, Beverage.State.FAILED);
    }

    // Orders emitter (orders)
    @Autowired @Channel("orders") Emitter<Order> orders;
    // Queue emitter (beverages)
    @Autowired @Channel("queue") Emitter<Beverage> queue;

    @PostMapping("/messaging")
    public Order messaging(Order order) {
        order = order.setOrderId(getId());
        queue.send(Beverage.queued(order));
        orders.send(order);
        return order;
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
