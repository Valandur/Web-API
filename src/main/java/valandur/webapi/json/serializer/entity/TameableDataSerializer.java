package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class TameableDataSerializer extends WebAPIBaseSerializer<TameableData> {
    @Override
    public void serialize(TameableData value) throws IOException {
        writeStartObject();
        writeField("isTamed", value.owner().exists());

        UUID uuid = value.owner().getDirect().orElse(Optional.empty()).orElse(null);
        if (uuid != null) {
            ICacheService srv = WebAPI.getCacheService();
            ICachedPlayer owner = srv.getPlayer(uuid).orElse(null);
            writeField("owner", owner);
        }
        writeEndObject();
    }
}
