package valandur.webapi.json.serializer.misc;

import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedLocation;

import java.io.IOException;

public class CachedLocationSerializer extends WebAPIBaseSerializer<CachedLocation> {
    @Override
    public void serialize(CachedLocation value) throws IOException {
        writeStartObject();
        writeField("world", value.getWorld(), Tristate.FALSE);
        writeField("position", value.getPosition());
        writeEndObject();
    }
}
