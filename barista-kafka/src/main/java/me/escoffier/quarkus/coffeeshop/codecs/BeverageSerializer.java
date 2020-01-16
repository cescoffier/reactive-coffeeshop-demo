package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.Beverage;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

@RegisterForReflection
public class BeverageSerializer extends JsonbCodec implements Serializer<Beverage> {

    @Override
    public byte[] serialize(String s, Beverage beverage) {
        return json.toJson(beverage).getBytes(StandardCharsets.UTF_8);
    }

}
