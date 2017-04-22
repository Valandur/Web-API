package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;

import java.io.IOException;

public class AgeableDataSerializer extends StdSerializer<AgeableData> {

    public AgeableDataSerializer() {
        this(null);
    }

    public AgeableDataSerializer(Class<AgeableData> t) {
        super(t);
    }

    @Override
    public void serialize(AgeableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("adult", value.adult().get());
        gen.writeNumberField("age", value.age().get());
        gen.writeEndObject();
    }
}
