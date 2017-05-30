package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class TameableDataSerializer extends WebAPISerializer<TameableData> {
    @Override
    public void serialize(TameableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "isTamed", value.owner().exists());

        UUID uuid = value.owner().getDirect().orElse(Optional.empty()).orElse(null);
        if (uuid != null) {
            CachedEntity owner = DataCache.getPlayer(uuid).orElse(null);
            if (owner == null) owner = DataCache.getEntity(uuid).orElse(null);

            writeField(provider, "owner", owner);
        }
        gen.writeEndObject();
    }
}
