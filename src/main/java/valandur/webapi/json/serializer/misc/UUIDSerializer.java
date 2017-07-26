package valandur.webapi.json.serializer.misc;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.util.UUID;

public class UUIDSerializer extends WebAPIBaseSerializer<UUID> {
    @Override
    public void serialize(UUID value) throws IOException {
        writeValue(value.toString());
    }
}
