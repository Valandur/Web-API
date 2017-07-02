package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Dimension;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class DimensionSerializer extends WebAPIBaseSerializer<Dimension> {
    @Override
    public void serialize(Dimension value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "type", new CachedCatalogType(value.getType()));
        gen.writeEndObject();
    }
}
