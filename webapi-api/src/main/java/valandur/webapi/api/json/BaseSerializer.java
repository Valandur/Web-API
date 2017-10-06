package valandur.webapi.api.json;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.jodah.typetools.TypeResolver;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.permission.IPermissionService;

/**
 * The base serializer that other serializers must inherit from.
 * This serializer exposes the Web-API services as class variables for convenience.
 * @param <T>
 */
public abstract class BaseSerializer<T> extends StdSerializer<T> {

    protected ICacheService cacheService;
    protected IJsonService jsonService;
    protected IPermissionService permissionService;

    private Class clazz;

    /**
     * Gets the class that is handled by this serializer.
     * @return The class handled by this serializer.
     */
    public Class getHandledClass() {
        return clazz;
    }


    public BaseSerializer() {
        this(null);
        this.clazz = TypeResolver.resolveRawArgument(BaseSerializer.class, getClass());
        this.cacheService = WebAPIAPI.getCacheService().orElse(null);
        this.jsonService = WebAPIAPI.getJsonService().orElse(null);
        this.permissionService = WebAPIAPI.getPermissionService().orElse(null);
    }
    private BaseSerializer(Class<T> t) {
        super(t);
    }
}
