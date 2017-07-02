package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class EntitySerializer extends WebAPIBaseSerializer<Entity> {
    @Override
    public void serialize(Entity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value instanceof Player) {
            writeValue(provider, WebAPI.getCacheService().getPlayer((Player)value));
        } else {
            writeValue(provider, WebAPI.getCacheService().getEntity(value));
        }
    }
}
