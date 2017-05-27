package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.world.CachedWorldBorder;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CachedWorldBorderSerializer extends WebAPISerializer<CachedWorldBorder> {
    @Override
    public void serialize(CachedWorldBorder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "center", value.getCenter());
        writeField(provider, "diameter", value.getDiameter());
        writeField(provider, "damageAmount", value.getDamageAmount());
        writeField(provider, "damageThreshold", value.getDamageThreshold());
        writeField(provider, "newDiameter", value.getNewDiameter());
        writeField(provider, "timeRemaining", value.getTimeRemaining());
        writeField(provider, "warningDistance", value.getWarningDistance());
        writeField(provider, "warningTime", value.getWarningTime());
        gen.writeEndObject();
    }
}
