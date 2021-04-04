package me.escoffier.quarkus.coffeeshop;

import io.quarkus.vault.VaultTransitSecretEngine;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

import static me.escoffier.quarkus.coffeeshop.Names.pickAName;

@ApplicationScoped
public class KafkaBarista {

    private static final Logger LOGGER = LoggerFactory.getLogger("Kafka-Barista");

    private String name = pickAName();

    @Inject
    VaultTransitSecretEngine vaultTransitSecretEngine;

    @ConfigProperty(name = "billing-code")
    String billingCode;

    @Incoming("orders")
    @Outgoing("queue")
    @Blocking
    public Beverage process(Order order) {
        return prepare(order);
    }

    Beverage prepare(Order order) {
        int delay = getPreparationTime();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Order {} for {} is ready", order.getProduct(), order.getName());
        LOGGER.info("Decrypting Credit Card Number {}", order.getCreditCard());
        LOGGER.info("Billing Credit Card {} using {} Billing Code", decryptCreditCard(order), billingCode);
        return new Beverage(order, name, Beverage.State.READY);
    }

    private String decryptCreditCard(Order order) {
        return vaultTransitSecretEngine.decrypt("my-encryption-key", order.getCreditCard()).asString();
    }

    private Random random = new Random();

    int getPreparationTime() {
        return random.nextInt(5) * 1000;
    }

}
