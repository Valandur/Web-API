package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.player.CachedPlayer;

import java.io.IOException;
import java.util.Optional;

public class CachedPlayerDeserializer extends StdDeserializer<CachedPlayer> {

    public CachedPlayerDeserializer() {
        super(CachedPlayer.class);
    }

    @Override
    public CachedPlayer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String nameOrUuid = p.getValueAsString();
        Optional<CachedPlayer> optPlayer = WebAPI.getCacheService().getPlayer(nameOrUuid);
        return optPlayer.orElse(null);
    }
}
