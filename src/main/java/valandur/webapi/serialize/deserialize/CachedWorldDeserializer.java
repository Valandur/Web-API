package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.io.IOException;
import java.util.Optional;

public class CachedWorldDeserializer extends StdDeserializer<ICachedWorld> {

    public CachedWorldDeserializer() {
        super(ICachedWorld.class);
    }

    @Override
    public ICachedWorld deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String nameOrUuid = p.getValueAsString();
        Optional<ICachedWorld> optWorld = WebAPI.getCacheService().getWorld(nameOrUuid);
        return optWorld.orElse(null);
    }
}
