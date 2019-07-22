package valandur.webapi.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.eclipse.jetty.io.EofException;
import valandur.webapi.WebAPI;

import javax.ws.rs.InternalServerErrorException;
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
            WebAPI.getLogger().error("The cache class " + cacheClass.getName() +
                    " does not contain a constructor accepting one argument of type " +
                    handledClass.getName() + " (it's handled class)");
            WebAPI.sentryCapture(e);
        }
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) {
        try {
            Object inst = ctr.newInstance(value);
            gen.writeObject(inst);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IOException e) {
            if (gen.isClosed()) return; // Don't try and write the error if the stream is already closed
            if (value instanceof Throwable) return; // Don't throw an exception if we're already processing an exception
            if (e instanceof EofException) return; // Don't throw on EofExceptions, because likely the remote stream was closed
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
