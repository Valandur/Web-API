package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.UUID;

public class UUIDSerializer extends WebAPIBaseSerializer<UUID> {
    @Override
    public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.toString());
    }
}
