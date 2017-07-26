package valandur.webapi.json.serializer.world;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.world.CachedWorldBorder;

import java.io.IOException;

public class CachedWorldBorderSerializer extends WebAPIBaseSerializer<CachedWorldBorder> {
    @Override
    public void serialize(CachedWorldBorder value) throws IOException {
        writeStartObject();
        writeField("center", value.getCenter());
        writeField("diameter", value.getDiameter());
        writeField("damageAmount", value.getDamageAmount());
        writeField("damageThreshold", value.getDamageThreshold());
        writeField("newDiameter", value.getNewDiameter());
        writeField("timeRemaining", value.getTimeRemaining());
        writeField("warningDistance", value.getWarningDistance());
        writeField("warningTime", value.getWarningTime());
        writeEndObject();
    }
}
