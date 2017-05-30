package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.cache.misc.CachedLocation;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CachedLocationSerializer extends WebAPISerializer<CachedLocation> {
    @Override
    public void serialize(CachedLocation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "world", value.getWorld(), Tristate.FALSE);
        writeField(provider, "position", value.getPosition());
        gen.writeEndObject();
    }
}
