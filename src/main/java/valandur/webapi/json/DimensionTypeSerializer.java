package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.DimensionType;

import java.io.IOException;

public class DimensionTypeSerializer extends StdSerializer<DimensionType> {

    public DimensionTypeSerializer() {
        this(null);
    }

    public DimensionTypeSerializer(Class<DimensionType> t) {
        super(t);
    }

    @Override
    public void serialize(DimensionType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("dimensionClass", value.getDimensionClass().getName());
        gen.writeEndObject();
    }
}
