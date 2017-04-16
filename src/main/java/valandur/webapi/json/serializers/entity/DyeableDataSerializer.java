package valandur.webapi.json.serializers.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;

import java.io.IOException;

public class DyeableDataSerializer extends StdSerializer<DyeableData> {

    public DyeableDataSerializer() {
        this(null);
    }

    public DyeableDataSerializer(Class<DyeableData> t) {
        super(t);
    }

    @Override
    public void serialize(DyeableData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.type().get().getId());
    }
}
