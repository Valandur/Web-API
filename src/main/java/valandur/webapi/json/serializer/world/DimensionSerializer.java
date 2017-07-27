package valandur.webapi.json.serializer.world;

import org.spongepowered.api.world.Dimension;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.io.IOException;

public class DimensionSerializer extends WebAPIBaseSerializer<Dimension> {
    @Override
    public void serialize(Dimension value) throws IOException {
        writeStartObject();
        writeField("type", new CachedCatalogType(value.getType()));
        writeEndObject();
    }
}
