package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;

public class TupleSerializer extends WebAPISerializer<Tuple> {

    @Override
    public void serialize(Tuple value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "first", value.getFirst());
        writeField(provider, "second", value.getSecond());
        gen.writeEndObject();
    }
}
