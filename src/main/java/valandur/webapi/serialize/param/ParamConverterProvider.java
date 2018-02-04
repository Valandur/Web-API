package valandur.webapi.serialize.param;

import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.world.CachedWorld;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(ICachedWorld.class) || rawType.equals(CachedWorld.class)) {
            return (ParamConverter<T>) new WorldParamConverter();
        }
        return null;
    }
}
