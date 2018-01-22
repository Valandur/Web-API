package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;

import java.io.IOException;
import java.util.Optional;

public class CachedPlayerDeserializer extends StdDeserializer<ICachedPlayer> {

    public CachedPlayerDeserializer() {
        super(ICachedPlayer.class);
    }

    @Override
    public ICachedPlayer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String nameOrUuid = p.getValueAsString();
        Optional<ICachedPlayer> optPlayer = WebAPI.getCacheService().getPlayer(nameOrUuid);
        return optPlayer.orElse(null);
    }
}
