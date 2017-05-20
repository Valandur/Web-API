package valandur.webapi.json.serializers.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.misc.CachedLocation;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class CachedLocationSerializer extends WebAPISerializer<CachedLocation> {
    @Override
    public void serialize(CachedLocation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "world", value.getWorld());
        writeField(provider, "position", value.getPosition());
        gen.writeEndObject();
    }
}
