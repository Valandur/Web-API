package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import valandur.webapi.misc.Util;

import java.io.IOException;
import java.util.Map;

public class DataContainerSerializer extends StdSerializer<DataContainer> {

    public DataContainerSerializer() {
        this(null);
    }

    public DataContainerSerializer(Class<DataContainer> t) {
        super(t);
    }

    @Override
    public void serialize(DataContainer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        Map<DataQuery, Object> values = value.getValues(false);
        for (Map.Entry<DataQuery, Object> entry : values.entrySet()) {
            String key = Util.lowerFirst(entry.getKey().asString("."));
            if (key.equalsIgnoreCase("ContentVersion") || key.equalsIgnoreCase("UnsafeData"))
                continue;
            gen.writeObjectField(key, entry.getValue());
        }
        gen.writeEndObject();
    }
}
