package valandur.webapi.swagger;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import valandur.webapi.WebAPI;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class SwaggerModelConverter implements ModelConverter {

    private HashMap<Type, Property> simpleProps = new HashMap<>();

    @Override
    public Property resolveProperty(Type type, ModelConverterContext context,
                                    Annotation[] annotations, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            return chain.next().resolveProperty(type, context, annotations, chain);
        } else {
            return null;
        }
    }

    @Override
    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            // If we can find a cache/view object for this type, then use that for documentation,
            // because we can't annotate Sponge classes with @Swagger stuff
            if (type instanceof Class) {
                Optional<Class> optClass = WebAPI.getSerializeService().getViewFor((Class)type);
                if (optClass.isPresent()) {
                    return chain.next().resolve(optClass.get(), context, chain);
                }
            }
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }
}
