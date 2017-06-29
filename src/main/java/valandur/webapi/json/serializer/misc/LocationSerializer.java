package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.misc.CachedLocation;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class LocationSerializer extends WebAPISerializer<Location<World>> {
    @Override
    public void serialize(Location<World> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedLocation(value));
    }
}
