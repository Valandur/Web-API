package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedEntitySerializer extends WebAPISerializer<CachedEntity> {
    @Override
    public void serialize(CachedEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "type", value.getType());
        writeField(provider, "link", value.getLink());

        if (((AtomicBoolean)provider.getAttribute("details")).get()) {
            writeField(provider, "class", value.getClass().getName());
            writeField(provider, "location", value.getLocation(), Tristate.FALSE);
            writeField(provider, "rotation", value.getRotation());
            writeField(provider, "velocity", value.getVelocity());
            writeField(provider, "scale", value.getScale());

            writeField(provider, "inventory", value.getInventory());

            writeData(provider, value.getData());
        }

        gen.writeEndObject();
    }
}
