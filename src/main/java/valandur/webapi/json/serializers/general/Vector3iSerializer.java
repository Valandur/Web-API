package valandur.webapi.json.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.flowpowered.math.vector.Vector3i;

import java.io.IOException;

public class Vector3iSerializer extends StdSerializer<Vector3i> {

    public Vector3iSerializer() {
        this(null);
    }

    public Vector3iSerializer(Class<Vector3i> t) {
        super(t);
    }

    @Override
    public void serialize(Vector3i value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("y", value.getY());
        gen.writeNumberField("z", value.getZ());
        gen.writeEndObject();
    }
}
