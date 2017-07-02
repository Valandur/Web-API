package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class DyeableDataSerializer extends WebAPIBaseSerializer<DyeableData> {
    @Override
    public void serialize(DyeableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.type().get().getId());
    }
}
