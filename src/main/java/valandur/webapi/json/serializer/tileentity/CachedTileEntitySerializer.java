package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedTileEntitySerializer extends WebAPIBaseSerializer<CachedTileEntity> {
    @Override
    public void serialize(CachedTileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "type", value.getType());
        writeField(provider, "location", value.getLocation());
        writeField(provider, "link", value.getLink());

        if (shouldWriteDetails(provider)) {
            writeField(provider, "class", value.getClass().getName());
            writeField(provider, "inventory", value.getInventory());

            writeData(provider, value.getData());
        }

        gen.writeEndObject();
    }
}
