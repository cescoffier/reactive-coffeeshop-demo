package me.escoffier.quarkus.coffeeshop;


import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.vault.VaultTransitSecretEngine;
import io.quarkus.vault.transit.ClearData;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.util.List;

import static org.awaitility.Awaitility.await;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
class BaristaTest {
    
    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    VaultTransitSecretEngine vaultTransitSecretEngine;

    @BeforeAll
    public static void setup() {
        VaultTransitSecretEngine mock = Mockito.mock(VaultTransitSecretEngine.class);
        Mockito.when(mock.decrypt(Mockito.anyString(), Mockito.anyString())).thenReturn(new ClearData("hello"));
        QuarkusMock.installMockForType(mock, VaultTransitSecretEngine.class);
    }

    @Test
    void testProcessOrder() {
        InMemorySource<Order> orders = connector.source("orders");
        InMemorySink<Beverage> queue = connector.sink("queue");

        Order order = new Order();
        order.setProduct("coffee");
        order.setName("Coffee lover");
        order.setOrderId("1234");
        order.setCreditCard("vault:v1:e/SfOvVO8LeJyMDWKyUmg+mYvb2h44G3dlWy56SHoQ==");

        orders.send(order);
        
        await().<List<? extends Message<Beverage>>>until(queue::received, t -> t.size() == 1);

        Beverage queuedBeverage = queue.received().get(0).getPayload();
        Assertions.assertEquals(Beverage.State.READY, queuedBeverage.preparationState);
        Assertions.assertEquals("coffee", queuedBeverage.beverage);
        Assertions.assertEquals("Coffee lover", queuedBeverage.customer);
        Assertions.assertEquals("1234", queuedBeverage.orderId);
    }

}
