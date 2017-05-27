package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class Vector3dSerializer extends WebAPISerializer<Vector3d> {
    @Override
    public void serialize(Vector3d value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "x", value.getX());
        writeField(provider, "y", value.getY());
        writeField(provider, "z", value.getZ());
        gen.writeEndObject();
    }
}
