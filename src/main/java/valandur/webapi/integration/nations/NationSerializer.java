package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Nation;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.UUID;

public class NationSerializer extends WebAPIBaseSerializer<Nation> {

    @Override
    protected void serialize(Nation value) throws IOException {
        writeStartObject();
        writeField("uuid", value.getUUID());
        writeField("name", value.getName());
        writeField("tag", value.getTag());
        writeField("president", cacheService.getPlayer(value.getPresident()).orElse(null), Tristate.FALSE);

        if (shouldWriteDetails()) {
            writeField("realName", value.getRealName());
            writeField("upkeep", value.getUpkeep());
            writeField("taxes", value.getTaxes());
            writeField("flags", value.getFlags());

            if (writeArrayFieldStart("citizens")) {
                for (UUID uuid : value.getCitizens()) {
                    writeValue(cacheService.getPlayer(uuid).orElse(null), Tristate.FALSE);
                }
                writeEndArray();
            }

            if (writeArrayFieldStart("ministers")) {
                for (UUID uuid : value.getMinisters()) {
                    writeValue(cacheService.getPlayer(uuid).orElse(null), Tristate.FALSE);
                }
                writeEndArray();
            }

            if (writeArrayFieldStart("staff")) {
                for (UUID uuid : value.getStaff()) {
                    writeValue(cacheService.getPlayer(uuid).orElse(null), Tristate.FALSE);
                }
                writeEndArray();
            }

            writeField("spawns", value.getSpawns().values());
            writeField("rects", value.getRegion().getRects());
            writeField("zones", value.getZones().values());
        }

        writeEndObject();
    }
}
