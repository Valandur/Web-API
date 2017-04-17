package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.GeneratorType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GeneratorTypeSerializer extends StdSerializer<GeneratorType> {

    public GeneratorTypeSerializer() {
        this(null);
    }

    public GeneratorTypeSerializer(Class<GeneratorType> t) {
        super(t);
    }

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
