package valandur.webapi.json.serializer.misc;

import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class Vector3iSerializer extends WebAPIBaseSerializer<Vector3i> {
    @Override
    public void serialize(Vector3i value) throws IOException {
        writeStartObject();
        writeField("x", value.getX());
        writeField("y", value.getY());
        writeField("z", value.getZ());
        writeEndObject();
    }
}
