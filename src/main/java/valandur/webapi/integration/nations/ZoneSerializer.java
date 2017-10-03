package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Zone;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.UUID;

public class ZoneSerializer extends WebAPIBaseSerializer<Zone> {

    @Override
    protected void serialize(Zone value) throws IOException {
        writeStartObject();

        writeField("uuid", value.getUUID());
        writeField("name", value.getName());
        writeField("rect", value.getRect());

        writeField("owner", cacheService.getPlayer(value.getOwner()).orElse(null), Tristate.FALSE);

        if (writeArrayFieldStart("coowners")) {
            for (UUID uuid : value.getCoowners()) {
                writeValue(cacheService.getPlayer(uuid).orElse(null), Tristate.FALSE);
            }
        }

        writeField("flags", value.getFlags());
        writeField("perms", value.getPerms());

        writeField("isForSale", value.isForSale());
        writeField("price", value.getPrice());

        writeEndObject();
    }
}
