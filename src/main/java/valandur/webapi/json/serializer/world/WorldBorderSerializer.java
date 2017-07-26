package valandur.webapi.json.serializer.world;

import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedWorldBorder;

import java.io.IOException;

public class WorldBorderSerializer extends WebAPIBaseSerializer<WorldBorder> {
    @Override
    public void serialize(WorldBorder value) throws IOException {
        writeValue(new CachedWorldBorder(value));
    }
}
