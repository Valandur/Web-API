package valandur.webapi.json.serializer.misc;

import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class Vector3dSerializer extends WebAPIBaseSerializer<Vector3d> {
    @Override
    public void serialize(Vector3d value) throws IOException {
        writeStartObject();
        writeField("x", value.getX());
        writeField("y", value.getY());
        writeField("z", value.getZ());
        writeEndObject();
    }
}
