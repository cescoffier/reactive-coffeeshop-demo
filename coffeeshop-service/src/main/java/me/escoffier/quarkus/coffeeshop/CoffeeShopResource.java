package me.escoffier.quarkus.coffeeshop;

import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class CoffeeShopResource {

    @Autowired
    @RestClient
    BaristaService barista;

    @PostMapping("/http")
    public Beverage http(Order order) {
        return barista.order(order.setOrderId(getId()));
    }

    // Orders emitter (Order)
    @Autowired @Channel("orders") Emitter<Order> orders;

    // Queue emitter (Beverage)
    @Autowired @Channel("queue") Emitter<Beverage> beverages;

    @PostMapping("/messaging")
    public Order messaging(Order order) {
        order.setOrderId(getId());
        beverages.send(Beverage.queued(order));
        orders.send(order);
        return order;
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
