package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ExceptionSerializer extends WebAPIBaseSerializer<Exception> {
    @Override
    public void serialize(Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "error", value.getMessage());
        gen.writeEndObject();
    }
}
