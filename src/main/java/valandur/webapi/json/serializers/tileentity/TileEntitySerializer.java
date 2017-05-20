package valandur.webapi.json.serializers.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class TileEntitySerializer extends WebAPISerializer<TileEntity> {
    @Override
    public void serialize(TileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, DataCache.getTileEntity(value.getLocation()).orElse(null));
    }
}
