package valandur.webapi.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BaseSerializer<T, U> extends StdSerializer<T> {

    private Class<? extends T> clazz;
    private Class<? extends U> cache;
    private Constructor ctr;

    public Class<? extends T> getHandledClass() {
        return clazz;
    }
    public Class<? extends U> getCacheClass() {
        return cache;
    }


    public BaseSerializer(Class<T> handledClass, Class<U> cacheClass) {
        super(handledClass);

        this.clazz = handledClass;
        this.cache = cacheClass;
        try {
            this.ctr = cache.getDeclaredConstructor(clazz);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        try {
            Object inst = ctr.newInstance(value);
            gen.writeObject(inst);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
