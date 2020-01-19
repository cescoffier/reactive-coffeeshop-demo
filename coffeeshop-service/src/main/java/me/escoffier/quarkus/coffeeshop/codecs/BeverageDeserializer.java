package me.escoffier.quarkus.coffeeshop.codecs;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import me.escoffier.quarkus.coffeeshop.model.Beverage;

@RegisterForReflection
public class BeverageDeserializer extends JsonbDeserializer<Beverage> {

    public BeverageDeserializer() {
        super(Beverage.class);
    }
}
