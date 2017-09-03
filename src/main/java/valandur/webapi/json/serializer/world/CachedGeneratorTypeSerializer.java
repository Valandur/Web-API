package valandur.webapi.json.serializer.world;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedGeneratorType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CachedGeneratorTypeSerializer extends WebAPIBaseSerializer<CachedGeneratorType> {
    @Override
    public void serialize(CachedGeneratorType value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("name", value.getName());
        writeField("class", value.getObjectClass().getName());
        Map<String, Object> settings = new HashMap<>();
        for (Map.Entry<String, Object> entry : value.getSettings().entrySet()) {
            writeField(entry.getKey(), entry.getValue());
        }
        writeField("settings", settings);
        writeEndObject();
    }
}
