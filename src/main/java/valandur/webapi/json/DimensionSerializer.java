package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.world.Dimension;

import java.io.IOException;

public class DimensionSerializer extends StdSerializer<Dimension> {

    public DimensionSerializer() {
        this(null);
    }

    public DimensionSerializer(Class<Dimension> t) {
        super(t);
    }

    @Override
    public void serialize(Dimension value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("type", value.getType());
        gen.writeNumberField("height", value.getHeight());
        gen.writeNumberField("buildHeight", value.getBuildHeight());
        gen.writeObjectField("generator", value.getGeneratorType());
        gen.writeEndObject();
    }
}
