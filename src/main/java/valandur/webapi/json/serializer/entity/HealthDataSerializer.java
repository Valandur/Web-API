package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class HealthDataSerializer extends WebAPISerializer<HealthData> {
    @Override
    public void serialize(HealthData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "current", value.health().get());
        writeField(provider, "max", value.maxHealth().get());
        gen.writeEndObject();
    }
}
