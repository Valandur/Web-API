package valandur.webapi.json.serializer.misc;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ExceptionSerializer extends WebAPIBaseSerializer<Exception> {
    @Override
    public void serialize(Exception value) throws IOException {
        writeStartObject();
        writeField("error", value.getMessage());
        writeEndObject();
    }
}
