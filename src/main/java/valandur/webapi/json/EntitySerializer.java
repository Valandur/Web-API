package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.cache.DataCache;

import java.io.IOException;

public class EntitySerializer extends StdSerializer<Entity> {

    public EntitySerializer() {
        this(null);
    }

    public EntitySerializer(Class<Entity> t) {
        super(t);
    }

    @Override
    public void serialize(Entity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean details = provider.isEnabled(MapperFeature.AUTO_DETECT_CREATORS);
        gen.writeRawValue(JsonConverter.toString(DataCache.getEntity(value), details));
    }
}
