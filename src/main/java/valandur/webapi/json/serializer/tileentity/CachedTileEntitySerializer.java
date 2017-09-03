package valandur.webapi.json.serializer.tileentity;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.tileentity.CachedTileEntity;

import java.io.IOException;

public class CachedTileEntitySerializer extends WebAPIBaseSerializer<CachedTileEntity> {
    @Override
    public void serialize(CachedTileEntity value) throws IOException {
        writeStartObject();

        writeField("type", value.getType());
        writeField("location", value.getLocation());
        writeField("link", value.getLink());

        if (shouldWriteDetails()) {
            writeField("class", value.getObjectClass().getName());
            writeField("inventory", value.getInventory());

            writeData(value.getData());
        }

        writeEndObject();
    }
}
