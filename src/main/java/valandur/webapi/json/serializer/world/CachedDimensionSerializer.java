package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.world.CachedDimension;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CachedDimensionSerializer extends WebAPISerializer<CachedDimension> {
    @Override
    public void serialize(CachedDimension value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "type", value.getType());
        writeField(provider, "height", value.getHeight());
        writeField(provider, "buildHeight", value.getBuildHeight());
        gen.writeEndObject();
    }
}
