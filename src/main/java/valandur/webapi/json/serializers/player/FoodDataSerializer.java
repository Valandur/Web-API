package valandur.webapi.json.serializers.player;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;

import java.io.IOException;

public class FoodDataSerializer extends StdSerializer<FoodData> {

    public FoodDataSerializer() {
        this(null);
    }

    public FoodDataSerializer(Class<FoodData> t) {
        super(t);
    }

    @Override
    public void serialize(FoodData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("foodLevel", value.foodLevel().get());
        gen.writeNumberField("exhaustion", value.exhaustion().get());
        gen.writeNumberField("saturation", value.saturation().get());
        gen.writeEndObject();
    }
}
