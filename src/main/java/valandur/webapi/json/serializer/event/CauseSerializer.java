package valandur.webapi.json.serializer.event;

import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedCause;

import java.io.IOException;

public class CauseSerializer extends WebAPIBaseSerializer<Cause> {

    @Override
    public void serialize(Cause value) throws IOException {
        writeValue(new CachedCause(value));
    }
}
