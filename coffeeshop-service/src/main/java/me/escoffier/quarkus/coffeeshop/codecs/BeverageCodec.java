package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.model.Beverage;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

@RegisterForReflection
public class BeverageCodec extends JsonbCodec implements Deserializer<Beverage>, Serializer<Beverage> {

    @Override
    public Beverage deserialize(String topic, byte[] data) {
        return json.fromJson(new String(data, StandardCharsets.UTF_8), Beverage.class);
    }

    @Override
    public byte[] serialize(String topic, Beverage beverage) {
        return json.toJson(beverage).getBytes(StandardCharsets.UTF_8);
    }
}
