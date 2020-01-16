package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.Order;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@RegisterForReflection
public class OrderDeserializer extends JsonbCodec implements Deserializer<Order> {

    @Override
    public Order deserialize(String topic, byte[] data) {
        return json.fromJson(new String(data, StandardCharsets.UTF_8), Order.class);
    }

}
