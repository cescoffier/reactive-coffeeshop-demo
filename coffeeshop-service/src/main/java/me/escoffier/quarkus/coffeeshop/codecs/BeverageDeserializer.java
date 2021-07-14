package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.model.Beverage;

@RegisterForReflection
public class BeverageDeserializer extends ObjectMapperDeserializer<Beverage> {

    public BeverageDeserializer() {
        super(Beverage.class);
    }
}
