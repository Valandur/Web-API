package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.io.IOException;
import java.util.Optional;

public class LocationDeserializer extends StdDeserializer<Location<World>> {

    public LocationDeserializer() {
        super(Location.class);
    }

    @Override
    public Location<World> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.readValueAsTree();

        Optional<ICachedWorld> optCache = WebAPI.getCacheService().getWorld(root.path("world").asText());
        if (!optCache.isPresent())
            throw new IOException("Invalid world name / uuid");

        JsonNode posNode = root.path("position");
        if (posNode.isMissingNode())
            throw new IOException("Invalid position");

        double x = posNode.path("x").asDouble();
        double y = posNode.path("y").asDouble();
        double z = posNode.path("z").asDouble();

        Optional<World> optWorld = optCache.get().getLive();
        if (!optWorld.isPresent())
            throw new IOException("Could not get world");

        return new Location<World>(optWorld.get(), x, y, z);
    }
}
