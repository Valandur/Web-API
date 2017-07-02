package valandur.webapi.json.serializer.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class TileEntitySerializer extends WebAPIBaseSerializer<TileEntity> {
    @Override
    public void serialize(TileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, WebAPI.getCacheService().getTileEntity(value.getLocation()).orElse(null));
    }
}
