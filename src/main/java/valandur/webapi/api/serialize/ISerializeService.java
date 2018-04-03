package valandur.webapi.api.serialize;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.api.cache.ICachedObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * The json service is used to convert java objects into json.
 * You can register your own serializers here to determine how certain objects are converted into json.
 */
public interface ISerializeService {

    /**
     * Registers an object as a cached object for the Web-API.
     * @param handledClass The class of the live object which is turned into a cached object.
     * @param cacheClass The class of the cached object.
     * @param <T> The type of the live object.
     */
    <T> void registerCache(Class<? extends T> handledClass, Class<? extends ICachedObject<T>> cacheClass);

    /**
     * Registers a view which helps turn an object into json.
     * @param handledClass The class of the live object which is turned into json using the view class.
     * @param viewClass The class of the view which helps turn the object into json.
     * @param <T> The type of the live object.
     */
    <T> void registerView(Class<? extends T> handledClass, Class<? extends BaseView<T>> viewClass);

    /**
     * Gets all DataHolder types that are supported by the Web-API
     * @return A map from json key to DataHolder type
     */
    Map<String, Class<? extends DataManipulator<?, ?>>> getSupportedData();

    /**
     * Gets all PropertyHolder types that are supported by the Web-API
     * @return A map from json key to PropertyHolder type
     */
    Map<Class<? extends Property<?, ?>>, String> getSupportedProperties();

    /**
     * Gets the View class used to represent a certain class, if present
     * @param clazz The class which should be check for an available view
     * @return The view class that should be used for serialization instead of the original class, if available.
     */
    Optional<Type> getViewFor(Class clazz);
}
