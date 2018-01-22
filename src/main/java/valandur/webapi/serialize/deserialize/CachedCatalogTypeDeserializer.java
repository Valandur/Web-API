package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.io.IOException;
import java.util.Optional;

public class CachedCatalogTypeDeserializer<T extends CatalogType> extends StdDeserializer<CachedCatalogType<T>>
        implements ContextualDeserializer {

    public CachedCatalogTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Class c = property.getType().containedType(0).getRawClass();
        return new CachedCatalogTypeDeserializer<>(c);
    }

    @Override
    public CachedCatalogType<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String id = p.getValueAsString();
        if (id == null) return null;
        Optional<T> type = Sponge.getRegistry().getType((Class<T>) _valueClass, id);
        return type.map(CachedCatalogType::new).orElse(null);
    }
}
