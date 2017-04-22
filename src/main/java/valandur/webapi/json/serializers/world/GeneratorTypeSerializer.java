package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.GeneratorType;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GeneratorTypeSerializer extends WebAPISerializer<GeneratorType> {
    @Override
    public void serialize(GeneratorType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeStringField("name", value.getName());
        Map<String, Object> settings = new HashMap<>();
        for (DataQuery query : value.getGeneratorSettings().getKeys(true)) {
            Optional val = value.getGeneratorSettings().get(query);
            if (!val.isPresent())
                continue;

            settings.put(query.asString("."), val.get());
        }
        gen.writeObjectField("settings", settings);
        gen.writeEndObject();
    }
}
