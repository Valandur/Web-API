package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CachedCatalogTypeSerializer extends WebAPISerializer<CachedCatalogType> {
    @Override
    public void serialize(CachedCatalogType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "id", value.getId());
        writeField(provider, "name", value.getName());
        gen.writeEndObject();
    }
}
