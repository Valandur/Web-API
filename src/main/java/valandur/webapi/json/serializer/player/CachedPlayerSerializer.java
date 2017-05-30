package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedPlayerSerializer extends WebAPISerializer<CachedPlayer> {
    @Override
    public void serialize(CachedPlayer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "name", value.getName());
        writeField(provider, "type", value.getType());
        writeField(provider, "link", value.getLink());
        writeField(provider, "location", value.getLocation());

        if (((AtomicBoolean)provider.getAttribute("details")).get()) {
            writeField(provider, "class", value.getClass().getName());
            writeField(provider, "rotation", value.getRotation());
            writeField(provider, "velocity", value.getVelocity());
            writeField(provider, "scale", value.getScale());

            gen.writeObjectFieldStart("connection");
            writeField(provider, "latency", value.getLatency());
            writeField(provider, "address", value.getAddress());
            gen.writeEndObject();

            gen.writeObjectFieldStart("armour");
            writeField(provider, "helmet", value.getHelmet());
            writeField(provider, "chestplate", value.getChestplate());
            writeField(provider, "leggings", value.getLeggings());
            writeField(provider, "boots", value.getBoots());
            gen.writeEndObject();

            writeField(provider, "inventory", value.getInventory());

            writeData(provider, value.getData());
        }

        gen.writeEndObject();
    }
}
