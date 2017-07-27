package valandur.webapi.json.serializer.misc;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedLocation;

import java.io.IOException;

public class LocationSerializer extends WebAPIBaseSerializer<Location<World>> {
    @Override
    public void serialize(Location<World> value) throws IOException {
        writeValue(new CachedLocation(value));
    }
}
