package valandur.webapi.json.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;

import java.io.IOException;

public class VelocityDataSerializer extends StdSerializer<VelocityData> {

    public VelocityDataSerializer() {
        this(null);
    }

    public VelocityDataSerializer(Class<VelocityData> t) {
        super(t);
    }

    @Override
    public void serialize(VelocityData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.velocity().get());
    }
}
