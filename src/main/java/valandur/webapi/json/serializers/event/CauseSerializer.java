package valandur.webapi.json.serializers.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.misc.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauseSerializer extends StdSerializer<Cause> {

    private static List<String> blockedCauseClasses;
    static {
        List<String> classes = new ArrayList<>();

        classes.add("net.minecraft.server");
        classes.add("valandur.webapi");

        blockedCauseClasses = classes;
    }

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
            String key = Util.lowerFirst(entry.getKey());
            if (blockedCauseClasses.stream().anyMatch(c -> entry.getValue().getClass().getName().startsWith(c)))
                gen.writeStringField(key, entry.getValue().getClass().getName());
            else
                gen.writeObjectField(key, entry.getValue());
        }

        gen.writeEndObject();
    }
}
