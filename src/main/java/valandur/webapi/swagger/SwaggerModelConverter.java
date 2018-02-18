package valandur.webapi.swagger;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class SwaggerModelConverter implements ModelConverter {

    @Override
    public Property resolveProperty(Type type, ModelConverterContext context,
                                    Annotation[] annotations, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            Class c = null;
            if (type instanceof SimpleType) {
                c = ((SimpleType) type).getRawClass();
            } else if (type instanceof Class) {
                c = (Class) type;
            }

            if (c != null) {
                // Process @JsonValue annotation on field first if we have any
                Optional<Field> optField = Arrays.stream(c.getFields())
                        .filter(f -> f.getAnnotation(JsonValue.class) != null)
                        .findAny();
                if (optField.isPresent()) {
                    return resolveProperty(optField.get().getType(), context, annotations, chain);
                }

                // Process @JsonValue annotation on method first if we have any
                Optional<Method> optMethod = Arrays.stream(c.getMethods())
                        .filter(m -> m.getAnnotation(JsonValue.class) != null)
                        .findAny();
                if (optMethod.isPresent()) {
                    return resolveProperty(optMethod.get().getReturnType(), context, annotations, chain);
                }

                // If we can find a cache/view object for this type, then use that for documentation,
                // because we can't annotate Sponge classes with @Swagger stuff
                Optional<Class> optClass = WebAPI.getSerializeService().getViewFor(c);
                if (optClass.isPresent()) {
                    return resolveProperty(optClass.get(), context, annotations, chain);
                }
            }
            return chain.next().resolveProperty(type, context, annotations, chain);
        } else {
            return null;
        }
    }

    @Override
    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            Class c = null;
            if (type instanceof SimpleType) {
                c = ((SimpleType) type).getRawClass();
            } else if (type instanceof Class) {
                c = (Class) type;
            }

            if (c != null) {
                // Process @JsonValue annotation on field first if we have any
                Optional<Field> optField = Arrays.stream(c.getFields())
                        .filter(f -> f.getAnnotation(JsonValue.class) != null)
                        .findAny();
                if (optField.isPresent()) {
                    return resolve(optField.get().getType(), context, chain);
                }

                // Process @JsonValue annotation on method first if we have any
                Optional<Method> optMethod = Arrays.stream(c.getMethods())
                        .filter(m -> m.getAnnotation(JsonValue.class) != null)
                        .findAny();
                if (optMethod.isPresent()) {
                    return resolve(optMethod.get().getReturnType(), context, chain);
                }

                // If we can find a cache/view object for this type, then use that for documentation,
                // because we can't annotate Sponge classes with @Swagger stuff
                Optional<Class> optClass = WebAPI.getSerializeService().getViewFor(c);
                if (optClass.isPresent()) {
                    return resolve(optClass.get(), context, chain);
                }
            }
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }
}
