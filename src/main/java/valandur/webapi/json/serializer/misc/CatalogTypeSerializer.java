package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.CatalogType;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CatalogTypeSerializer extends WebAPIBaseSerializer<CatalogType> {
    @Override
    public void serialize(CatalogType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedCatalogType(value));
    }
}
