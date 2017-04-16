package valandur.webapi.json.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.flowpowered.math.vector.Vector3d;

import java.io.IOException;

public class Vector3dSerializer extends StdSerializer<Vector3d> {

    public Vector3dSerializer() {
        this(null);
    }

    public Vector3dSerializer(Class<Vector3d> t) {
        super(t);
    }

    @Override
    public void serialize(Vector3d value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("y", value.getY());
        gen.writeNumberField("z", value.getZ());
        gen.writeEndObject();
    }
}
