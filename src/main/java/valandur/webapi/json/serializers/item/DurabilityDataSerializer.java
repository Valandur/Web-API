package valandur.webapi.json.serializers.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;

import java.io.IOException;

public class DurabilityDataSerializer extends StdSerializer<DurabilityData> {

    public DurabilityDataSerializer() {
        this(null);
    }

    public DurabilityDataSerializer(Class<DurabilityData> t) {
        super(t);
    }

    @Override
    public void serialize(DurabilityData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("unbreakable", value.unbreakable().get());
        gen.writeNumberField("durability", value.durability().get());
        gen.writeEndObject();
    }
}
