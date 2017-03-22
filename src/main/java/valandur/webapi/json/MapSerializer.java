package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import valandur.webapi.misc.Util;

import java.io.IOException;
import java.util.Map;

public class MapSerializer extends StdSerializer<Map> {

    public MapSerializer() {
        this(null);
    }

    public MapSerializer(Class<Map> t) {
        super(t);
    }

    @Override
    public void serialize(Map value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for (Object entry : value.entrySet()) {
            if (!(entry instanceof Map.Entry)) {
                continue;
            }

            Map.Entry e = (Map.Entry)entry;
            String key = Util.lowerFirst(e.getKey().toString());
            if (key.equalsIgnoreCase("contentVersion"))
                continue;
            gen.writeObjectField(key, e.getValue());
        }
        gen.writeEndObject();
    }
}
