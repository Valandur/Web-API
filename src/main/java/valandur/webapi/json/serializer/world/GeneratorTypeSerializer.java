package valandur.webapi.json.serializer.world;

import org.spongepowered.api.world.GeneratorType;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedGeneratorType;

import java.io.IOException;

public class GeneratorTypeSerializer extends WebAPIBaseSerializer<GeneratorType> {
    @Override
    public void serialize(GeneratorType value) throws IOException {
        writeValue(new CachedGeneratorType(value));
    }
}
