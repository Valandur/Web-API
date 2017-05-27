package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedTileEntitySerializer extends WebAPISerializer<CachedTileEntity> {
    @Override
    public void serialize(CachedTileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "type", value.getType());
        writeField(provider, "location", value.getLocation());
        writeField(provider, "link", value.getLink());

        if (((AtomicBoolean)provider.getAttribute("details")).get()) {
            writeField(provider, "inventory", value.getInventory());

            writeData(provider, value.getData());
        }

        gen.writeEndObject();
    }
}
