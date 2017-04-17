package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.WorldBorder;

import java.io.IOException;

public class WorldBorderSerializer extends StdSerializer<WorldBorder> {

    public WorldBorderSerializer() {
        this(null);
    }

    public WorldBorderSerializer(Class<WorldBorder> t) {
        super(t);
    }

    @Override
    public void serialize(WorldBorder value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("center", value.getCenter());
        gen.writeNumberField("diameter", value.getDiameter());
        gen.writeNumberField("damageAmount", value.getDamageAmount());
        gen.writeNumberField("damageThreshold", value.getDamageThreshold());
        gen.writeNumberField("newDiameter", value.getNewDiameter());
        gen.writeNumberField("timeRemaining", value.getTimeRemaining());
        gen.writeNumberField("warningDistance", value.getWarningDistance());
        gen.writeNumberField("warningTime", value.getWarningTime());
        gen.writeEndObject();
    }
}
