package valandur.webapi.json.serializer.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class FoodDataSerializer extends WebAPIBaseSerializer<FoodData> {
    @Override
    public void serialize(FoodData value) throws IOException {
        writeStartObject();
        writeField("foodLevel", value.foodLevel().get());
        writeField("exhaustion", value.exhaustion().get());
        writeField("saturation", value.saturation().get());
        writeEndObject();
    }
}
