package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class AgeableDataSerializer extends WebAPIBaseSerializer<AgeableData> {
    @Override
    public void serialize(AgeableData value) throws IOException {
        writeStartObject();
        writeField("adult", value.adult().get());
        writeField("age", value.age().get());
        writeEndObject();
    }
}
