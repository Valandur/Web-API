package valandur.webapi.serialize.param;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.ICachedWorld;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (ICachedWorld.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new WorldParamConverter();
        } else if (ICachedPlayer.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new PlayerParamConverter();
        } else if (Vector3d.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new Vector3dParamConverter();
        } else if (Vector3i.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new Vector3iParamConverter();
        }
        return null;
    }
}
