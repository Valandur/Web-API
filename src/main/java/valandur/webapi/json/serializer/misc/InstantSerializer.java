package valandur.webapi.json.serializer.misc;

import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;
import java.time.Instant;

public class InstantSerializer extends WebAPIBaseSerializer<Instant> {

    @Override
    public void serialize(Instant value) throws IOException {
        writeValue(value.getEpochSecond());
    }
}
