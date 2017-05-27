package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.CareerData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CareerDataSerializer extends WebAPISerializer<CareerData> {
    @Override
    public void serialize(CareerData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.type().get());
    }
}
