package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.api.cache.misc.CachedCatalogType;

import java.io.IOException;
import java.util.Optional;

public class CachedCatalogTypeDeserializer<T extends CatalogType> extends StdDeserializer<CachedCatalogType<T>>
        implements ContextualDeserializer {

    public CachedCatalogTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        Class c = property.getType().containedType(0).getRawClass();
        return new CachedCatalogTypeDeserializer<>(c);
    }

    @Override
    public CachedCatalogType<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode tree = p.readValueAsTree();
        if (tree == null) return null;
        if (tree.path("id").isMissingNode()) return null;
        Optional<T> type = Sponge.getRegistry().getType((Class<T>) _valueClass, tree.get("id").asText());
        return type.map(CachedCatalogType::new).orElse(null);
    }
}
