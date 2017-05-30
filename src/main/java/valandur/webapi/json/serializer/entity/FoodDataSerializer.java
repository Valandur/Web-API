package valandur.webapi.json.serializer.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class FoodDataSerializer extends WebAPISerializer<FoodData> {
    @Override
    public void serialize(FoodData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "foodLevel", value.foodLevel().get());
        writeField(provider, "exhaustion", value.exhaustion().get());
        writeField(provider, "saturation", value.saturation().get());
        gen.writeEndObject();
    }
}
