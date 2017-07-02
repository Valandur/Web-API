package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class WorldSerializer extends WebAPIBaseSerializer<World> {
    @Override
    public void serialize(World value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, WebAPI.getCacheService().getWorld(value));
    }
}
