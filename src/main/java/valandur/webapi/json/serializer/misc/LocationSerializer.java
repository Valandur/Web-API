package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.misc.CachedLocation;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class LocationSerializer extends WebAPIBaseSerializer<Location<World>> {
    @Override
    public void serialize(Location<World> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedLocation(value));
    }
}
