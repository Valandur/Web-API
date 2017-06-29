package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.api.cache.world.CachedWorldBorder;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class WorldBorderSerializer extends WebAPISerializer<WorldBorder> {
    @Override
    public void serialize(WorldBorder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedWorldBorder(value));
    }
}
