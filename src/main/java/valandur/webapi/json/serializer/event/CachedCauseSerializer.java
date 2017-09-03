package valandur.webapi.json.serializer.event;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedCause;

import java.io.IOException;
import java.util.Map;

public class CachedCauseSerializer extends WebAPIBaseSerializer<CachedCause> {

    @Override
    protected void serialize(CachedCause value) throws IOException {
        writeStartObject();
        for (Map.Entry<String, Object> entry : value.getCauses().entrySet()) {
            writeField(entry.getKey(), entry.getValue());
        }
        writeEndObject();
    }
}
