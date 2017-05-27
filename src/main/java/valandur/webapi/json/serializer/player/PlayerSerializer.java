package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class PlayerSerializer extends WebAPISerializer<Player> {
    @Override
    public void serialize(Player value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, DataCache.getPlayer(value));
    }
}
