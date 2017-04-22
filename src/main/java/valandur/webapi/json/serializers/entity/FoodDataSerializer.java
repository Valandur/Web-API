package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class FoodDataSerializer extends WebAPISerializer<FoodData> {
    @Override
    public void serialize(FoodData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("foodLevel", value.foodLevel().get());
        gen.writeNumberField("exhaustion", value.exhaustion().get());
        gen.writeNumberField("saturation", value.saturation().get());
        gen.writeEndObject();
    }
}
