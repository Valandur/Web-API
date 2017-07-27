package valandur.webapi.json.serializer.misc;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.io.IOException;

public class CachedCatalogTypeSerializer extends WebAPIBaseSerializer<CachedCatalogType> {
    @Override
    public void serialize(CachedCatalogType value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("name", value.getName());
        writeEndObject();
    }
}
