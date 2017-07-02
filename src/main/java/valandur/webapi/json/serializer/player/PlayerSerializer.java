package valandur.webapi.json.serializer.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PlayerSerializer extends WebAPIBaseSerializer<Player> {
    @Override
    public void serialize(Player value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, WebAPI.getCacheService().getPlayer(value));
    }
}
