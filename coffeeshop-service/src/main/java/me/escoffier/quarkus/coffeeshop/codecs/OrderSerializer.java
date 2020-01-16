package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.model.Order;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

@RegisterForReflection
public class OrderSerializer extends JsonbCodec implements Serializer<Order> {

    @Override
    public byte[] serialize(String topic, Order order) {
        return json.toJson(order).getBytes(StandardCharsets.UTF_8);
    }

}
