package valandur.webapi.json.serializers.tileentity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class TileEntitySerializer extends WebAPISerializer<TileEntity> {
    @Override
    public void serialize(TileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean details = provider.isEnabled(MapperFeature.AUTO_DETECT_CREATORS);
        gen.writeRawValue(JsonConverter.toString(DataCache.getTileEntity(value.getLocation()), details));
    }
}
