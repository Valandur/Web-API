package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ShearedDataSerializer extends WebAPIBaseSerializer<ShearedData> {
    @Override
    public void serialize(ShearedData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.sheared().get());
    }
}
