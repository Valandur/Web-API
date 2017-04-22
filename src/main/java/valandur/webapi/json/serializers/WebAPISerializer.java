package valandur.webapi.json.serializers;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.jodah.typetools.TypeResolver;

public abstract class WebAPISerializer<T> extends StdSerializer<T> {

    private Class clazz;
    public Class getHandledClass() {
        return clazz;
    }

    public WebAPISerializer() {
        this(null);
        this.clazz = TypeResolver.resolveRawArgument(WebAPISerializer.class, getClass());
    }
    private WebAPISerializer(Class<T> t) {
        super(t);
    }
}
