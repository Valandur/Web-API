package valandur.webapi.api.serialize;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.util.TreeNode;

import java.io.IOException;
import java.io.Reader;
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
    Map<String, Class<? extends DataManipulator>> getSupportedData();

    /**
     * Converts an object directly to a json string. Includes details if specified.
     * @param obj The object to convert to json.
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json string representation of the object.
     */
    String toString(Object obj, boolean xml, boolean details, TreeNode<String, Boolean> perms);

    /**
     * Converts an object to json using the default object mapper. Includes details if specified.
     * @param obj The object to convert to json
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @param perms The permissions defining which properties of the object are returned.
     * @return The json representation of the object.
     */
    JsonNode serialize(Object obj, boolean xml, boolean details, TreeNode<String, Boolean> perms);

    /**
     * Converts data read from a reader to a json object tree.
     * @param reader The reader from which the json data is read.
     * @param perms The permissions defining which nodes of the tree will be included.
     * @return A {@code JsonNode} representing the tree object that was read.
     * @throws IOException Is thrown when parsing of the string fails.
     */
    JsonNode deserialize(Reader reader, boolean xml, TreeNode<String, Boolean> perms) throws IOException;

    /**
     * Converts a json string to an object of the specified java type using the object mapper.
     * @param content The json content that is parsed into an object.
     * @param type The type of the object.
     * @param perms The permissions node defining which nodes can be accessed.
     * @param <T> The type of the parsed object.
     * @return An object of the parsed type.
     * @throws IOException Is thrown when the provided content can't be parsed to the specified class.
     */
    <T> T deserialize(String content, boolean xml, JavaType type, TreeNode<String, Boolean> perms) throws IOException;

    /**
     * Converts the provided json node into an object of the specified class using the object mapper.
     * @param content The json node that is parsed into an object.
     * @param clazz The class of the object.
     * @param perms The permissions node defining which nodes can be accessed.
     * @param <T> The type of the parsed object.
     * @return An object of the parsed type.
     * @throws IOException Is thrown when the provided content can't be parsed to the specified class.
     */
    <T> T deserialize(JsonNode content, Class<T> clazz, TreeNode<String, Boolean> perms) throws IOException;

    /**
     * Converts a class structure to json. This includes all the fields and methods of the class
     * @param c The class for which to get the json representation.
     * @return A JsonNode representing the class.
     */
    JsonNode classToJson(Class c);
}
