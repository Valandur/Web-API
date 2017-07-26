package valandur.webapi.json.serializer.misc;

import org.spongepowered.api.CatalogType;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.io.IOException;

public class CatalogTypeSerializer extends WebAPIBaseSerializer<CatalogType> {
    @Override
    public void serialize(CatalogType value) throws IOException {
        writeValue(new CachedCatalogType(value));
    }
}
