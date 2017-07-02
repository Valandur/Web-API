package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class AgeableDataSerializer extends WebAPIBaseSerializer<AgeableData> {
    @Override
    public void serialize(AgeableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "adult", value.adult().get());
        writeField(provider, "age", value.age().get());
        gen.writeEndObject();
    }
}
