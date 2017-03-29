package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.event.cause.Cause;

import java.io.IOException;
import java.util.Map;

public class CauseSerializer extends StdSerializer<Cause> {

    public CauseSerializer() {
        this(null);
    }

    public CauseSerializer(Class<Cause> t) {
        super(t);
    }

    @Override
    public void serialize(Cause value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        for (Map.Entry<String, Object> entry : value.getNamedCauses().entrySet()) {
            if (entry.getValue().getClass().getName().startsWith("net.minecraft.server"))
                gen.writeStringField(entry.getKey(), entry.getValue().getClass().getName());
            else
                gen.writeObjectField(entry.getKey(), entry.getValue());
        }

        gen.writeEndObject();
    }
}
