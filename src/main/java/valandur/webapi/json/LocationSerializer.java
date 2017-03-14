package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedLocation;

import java.io.IOException;

public class LocationSerializer extends StdSerializer<Location<World>> {

    public LocationSerializer() {
        this(null);
    }

    public LocationSerializer(Class<Location<World>> t) {
        super(t);
    }

    @Override
    public void serialize(Location<World> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeRawValue(JsonConverter.toString(CachedLocation.copyFrom(value)));
    }
}
