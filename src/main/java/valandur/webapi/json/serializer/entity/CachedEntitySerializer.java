package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.cache.entity.CachedEntity;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class CachedEntitySerializer extends WebAPISerializer<CachedEntity> {
    @Override
    public void serialize(CachedEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "uuid", value.getUUID());
        writeField(provider, "type", value.getType());
        writeField(provider, "link", value.getLink());

        if (shouldWriteDetails(provider)) {
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
