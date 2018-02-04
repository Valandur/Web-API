package valandur.webapi.api.serialize;

import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.api.cache.ICachedObject;

import java.util.Map;

/**
 * The json service is used to convert java objects into json.
 * You can register your own serializers here to determine how certain objects are converted into json.
 * Custom serializers must inhert {@link BaseSerializer}.
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
}
