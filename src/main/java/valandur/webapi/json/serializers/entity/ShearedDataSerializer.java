package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;

import java.io.IOException;

public class ShearedDataSerializer extends StdSerializer<ShearedData> {

    public ShearedDataSerializer() {
        this(null);
    }

    public ShearedDataSerializer(Class<ShearedData> t) {
        super(t);
    }

    @Override
    public void serialize(ShearedData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeBoolean(value.sheared().get());
    }
}
