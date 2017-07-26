package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class HealthDataSerializer extends WebAPIBaseSerializer<HealthData> {
    @Override
    public void serialize(HealthData value) throws IOException {
        writeStartObject();
        writeField("current", value.health().get());
        writeField("max", value.maxHealth().get());
        writeEndObject();
    }
}
