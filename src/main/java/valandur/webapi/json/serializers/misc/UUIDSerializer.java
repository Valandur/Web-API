package valandur.webapi.json.serializers.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;
import java.util.UUID;

public class UUIDSerializer extends WebAPISerializer<UUID> {
    @Override
    public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, value.toString());
    }
}
