package valandur.webapi.json.serializer.world;

import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class WorldSerializer extends WebAPIBaseSerializer<World> {
    @Override
    public void serialize(World value) throws IOException {
        writeValue(WebAPI.getCacheService().getWorld(value));
    }
}
