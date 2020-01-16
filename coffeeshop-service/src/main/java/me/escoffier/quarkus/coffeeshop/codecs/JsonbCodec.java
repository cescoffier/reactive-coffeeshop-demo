package me.escoffier.quarkus.coffeeshop.codecs;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.Closeable;
import java.util.Map;

public class JsonbCodec implements Closeable {
    protected final Jsonb json = JsonbBuilder.create();

    public void configure(Map<String, ?> map, boolean b) {
        // No config.
    }

    @Override
    public void close() {
        try {
            json.close();
        } catch (Exception ignored) {
            // ignore me.
        }
    }
}
