package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.block.tileentity.TileEntity;
import valandur.webapi.cache.DataCache;

import java.io.IOException;

public class TileEntitySerializer extends StdSerializer<TileEntity> {

    public TileEntitySerializer() {
        this(null);
    }

    public TileEntitySerializer(Class<TileEntity> t) {
        super(t);
    }

    @Override
    public void serialize(TileEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean details = provider.isEnabled(MapperFeature.AUTO_DETECT_CREATORS);
        gen.writeRawValue(JsonConverter.toString(DataCache.getTileEntity(value.getLocation()), details));
    }
}
