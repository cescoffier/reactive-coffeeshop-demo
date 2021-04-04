package me.escoffier.quarkus.coffeeshop;

import io.quarkus.vault.VaultTransitSecretEngine;
import io.smallrye.mutiny.Uni;
import me.escoffier.quarkus.coffeeshop.http.BaristaService;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CoffeeShopResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeShopResource.class);

    @Autowired
    @RestClient
    BaristaService barista;

    @PostMapping("/http")
    public Uni<Beverage> http(Order order) {
        return barista.order(order.setOrderId(getId()))
                .onItem().invoke(beverage -> beverage.preparationState = Beverage.State.READY)
                .ifNoItem().after(Duration.ofMillis(1500)).fail()
                .onFailure().recoverWithItem(createFallbackBeverage(order));
    }

    private Beverage createFallbackBeverage(Order order) {
        return new Beverage(order, null, Beverage.State.FAILED);
    }

    // Orders emitter (orders)
    @Autowired @Channel("orders") Emitter<Order> orders;
    // Queue emitter (beverages)
    @Autowired @Channel("queue") Emitter<Beverage> queue;

    @Inject
    VaultTransitSecretEngine vaultTransitSecretEngine;

    @PostMapping("/messaging")
    public Order messaging(Order order) {
        order = order
                .setOrderId(getId())
                .setCreditCard(encryptCreditCard(order));
        queue.send(Beverage.queued(order));
        orders.send(order);
        return order;
    }

    private String encryptCreditCard(Order order) {
        return vaultTransitSecretEngine.encrypt("my-encryption-key", order.getCreditCard());
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

}
