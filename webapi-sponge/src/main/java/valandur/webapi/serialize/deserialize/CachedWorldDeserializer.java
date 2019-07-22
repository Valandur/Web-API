package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.world.CachedWorld;

import java.io.IOException;
import java.util.Optional;

public class CachedWorldDeserializer extends StdDeserializer<CachedWorld> {

    public CachedWorldDeserializer() {
        super(CachedWorld.class);
    }

    @Override
    public CachedWorld deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String nameOrUuid = p.getValueAsString();
        Optional<CachedWorld> optWorld = WebAPI.getCacheService().getWorld(nameOrUuid);
        return optWorld.orElse(null);
    }
}
