package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.CareerData;

import java.io.IOException;

public class CareerDataSerializer extends StdSerializer<CareerData> {

    public CareerDataSerializer() {
        this(null);
    }

    public CareerDataSerializer(Class<CareerData> t) {
        super(t);
    }

    @Override
    public void serialize(CareerData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.type().get());
    }
}
