package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ShearedDataSerializer extends WebAPISerializer<ShearedData> {
    @Override
    public void serialize(ShearedData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.sheared().get());
    }
}
