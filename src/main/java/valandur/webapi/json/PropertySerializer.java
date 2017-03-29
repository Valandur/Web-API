package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.Property;

import java.io.IOException;

public class PropertySerializer extends StdSerializer<Property<?, ?>> {

    public PropertySerializer() {
        this(null);
    }

    public PropertySerializer(Class<Property<?, ?>> t) {
        super(t);
    }

    @Override
    public void serialize(Property<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        //gen.writeObjectFieldStart(value.getKey().toString());
        //gen.writeEndObject();
    }
}
