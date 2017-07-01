package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPISerializer;

import java.io.IOException;
import java.time.Instant;

public class InstantSerializer extends WebAPISerializer<Instant> {

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.getEpochSecond());
    }
}
