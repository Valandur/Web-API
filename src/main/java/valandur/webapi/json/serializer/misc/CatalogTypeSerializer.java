package valandur.webapi.json.serializer.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.CatalogType;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class CatalogTypeSerializer extends WebAPISerializer<CatalogType> {
    @Override
    public void serialize(CatalogType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, new CachedCatalogType(value));
    }
}
