package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.GeneratorType;
import valandur.webapi.cache.world.CachedGeneratorType;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class GeneratorTypeSerializer extends WebAPISerializer<GeneratorType> {
    @Override
    public void serialize(GeneratorType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedGeneratorType(value));
    }
}
