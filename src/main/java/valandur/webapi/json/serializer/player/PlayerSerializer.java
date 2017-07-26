package valandur.webapi.json.serializer.player;

import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PlayerSerializer extends WebAPIBaseSerializer<Player> {
    @Override
    public void serialize(Player value) throws IOException {
        writeValue(WebAPI.getCacheService().getPlayer(value));
    }
}
