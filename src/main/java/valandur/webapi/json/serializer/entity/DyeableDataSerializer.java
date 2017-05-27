package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class DyeableDataSerializer extends WebAPISerializer<DyeableData> {
    @Override
    public void serialize(DyeableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.type().get().getId());
    }
}
