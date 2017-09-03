package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.entity.CachedEntity;

import java.io.IOException;

public class CachedEntitySerializer extends WebAPIBaseSerializer<CachedEntity> {
    @Override
    public void serialize(CachedEntity value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("type", value.getType());
        writeField("link", value.getLink());

        if (shouldWriteDetails()) {
            writeField("class", value.getObjectClass().getName());
            writeField("location", value.getLocation(), Tristate.FALSE);
            writeField("rotation", value.getRotation());
            writeField("velocity", value.getVelocity());
            writeField("scale", value.getScale());

            writeField("inventory", value.getInventory());

            writeData(value.getData());
        }

        writeEndObject();
    }
}
