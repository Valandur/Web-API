package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.world.CachedGeneratorType;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CachedGeneratorTypeSerializer extends WebAPISerializer<CachedGeneratorType> {
    @Override
    public void serialize(CachedGeneratorType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getId());
        writeField(provider, "name", value.getName());
        Map<String, Object> settings = new HashMap<>();
        for (Map.Entry<String, Object> entry : value.getSettings().entrySet()) {
            writeField(provider, entry.getKey(), entry.getValue());
        }
        writeField(provider, "settings", settings);
        gen.writeEndObject();
    }
}
