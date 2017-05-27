package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class EntitySerializer extends WebAPISerializer<Entity> {
    @Override
    public void serialize(Entity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value instanceof Player) {
            writeValue(provider, DataCache.getPlayer((Player)value));
        } else {
            writeValue(provider, DataCache.getEntity(value));
        }
    }
}
