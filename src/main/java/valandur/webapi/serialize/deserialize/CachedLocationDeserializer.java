package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.io.IOException;
import java.util.Optional;

public class CachedLocationDeserializer extends StdDeserializer<CachedLocation> {

    public CachedLocationDeserializer() {
        super(CachedLocation.class);
    }

    @Override
    public CachedLocation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.readValueAsTree();

        if (root.isNull() || root.isMissingNode())
            return null;

        Optional<ICachedWorld> optWorld = WebAPI.getCacheService().getWorld(root.path("world").asText());
        if (!optWorld.isPresent())
            throw new IOException("Invalid world name / uuid");

        JsonNode posNode = root.path("position");
        if (posNode.isMissingNode())
            throw new IOException("Invalid position");

        double x = posNode.path("x").asDouble();
        double y = posNode.path("y").asDouble();
        double z = posNode.path("z").asDouble();

        return new CachedLocation(optWorld.get(), x, y, z);
    }
}
