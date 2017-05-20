package valandur.webapi.json.serializers.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class Vector3iSerializer extends WebAPISerializer<Vector3i> {
    @Override
    public void serialize(Vector3i value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "x", value.getX());
        writeField(provider, "y", value.getY());
        writeField(provider, "z", value.getZ());
        gen.writeEndObject();
    }
}
