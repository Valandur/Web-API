package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.GeneratorType;

import java.io.IOException;

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
        gen.writeObjectField("settings", value.getGeneratorSettings());
        gen.writeEndObject();
    }
}
