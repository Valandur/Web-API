package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ExceptionSerializer extends WebAPISerializer<Exception> {
    @Override
    public void serialize(Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "error", value.getMessage());
        gen.writeEndObject();
    }
}
