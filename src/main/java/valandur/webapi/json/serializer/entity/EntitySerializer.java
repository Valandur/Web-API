package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class EntitySerializer extends WebAPIBaseSerializer<Entity> {
    @Override
    public void serialize(Entity value) throws IOException {
        if (value instanceof Player) {
            writeValue(WebAPI.getCacheService().getPlayer((Player)value));
        } else {
            writeValue(WebAPI.getCacheService().getEntity(value));
        }
    }
}
