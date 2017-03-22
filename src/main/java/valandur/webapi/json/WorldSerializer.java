package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.DataCache;

import java.io.IOException;

public class WorldSerializer extends StdSerializer<World> {

    public WorldSerializer() {
        this(null);
    }

    public WorldSerializer(Class<World> t) {
        super(t);
    }

    @Override
    public void serialize(World value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean details = provider.isEnabled(MapperFeature.AUTO_DETECT_CREATORS);
        gen.writeRawValue(JsonConverter.toString(DataCache.getWorld(value), details));
    }
}
