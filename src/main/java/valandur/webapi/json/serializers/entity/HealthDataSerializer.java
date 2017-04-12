package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;

import java.io.IOException;

public class HealthDataSerializer extends StdSerializer<HealthData> {

    public HealthDataSerializer() {
        this(null);
    }

    public HealthDataSerializer(Class<HealthData> t) {
        super(t);
    }

    @Override
    public void serialize(HealthData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("current", value.health().get());
        gen.writeObjectField("max", value.maxHealth().get());
        gen.writeEndObject();
    }
}
