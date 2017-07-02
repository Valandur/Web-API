package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class TameableDataSerializer extends WebAPIBaseSerializer<TameableData> {
    @Override
    public void serialize(TameableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "isTamed", value.owner().exists());

        UUID uuid = value.owner().getDirect().orElse(Optional.empty()).orElse(null);
        if (uuid != null) {
            ICacheService srv = WebAPI.getCacheService();
            ICachedEntity owner = srv.getPlayer(uuid).orElse(null);
            if (owner == null) owner = srv.getEntity(uuid).orElse(null);

            writeField(provider, "owner", owner);
        }
        gen.writeEndObject();
    }
}
