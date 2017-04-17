package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;

import java.io.IOException;

public class SpawnableDataSerializer extends StdSerializer<SpawnableData> {

    public SpawnableDataSerializer() {
        this(null);
    }

    public SpawnableDataSerializer(Class<SpawnableData> t) {
        super(t);
    }

    @Override
    public void serialize(SpawnableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.type().get().getId());
    }
}
