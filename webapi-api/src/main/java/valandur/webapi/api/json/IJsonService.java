package valandur.webapi.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import valandur.webapi.api.util.TreeNode;

import java.util.Map;

/**
 * The json service is used to convert java objects into json.
 * You can register your own serializers here to determine how certain objects are converted into json.
 * Custom serializers must inhert {@link WebAPIBaseSerializer}.
 */
public interface IJsonService {

    /**
     * Registers a new serializer that will be used to turn objects into json
     * @param clazz The class that this serializer is registered for
     * @param serializer The class of serializer to use for the specified class
     * @param <T> The class to serialize
     */
    <T> void registerSerializer(Class<T> clazz, Class<? extends WebAPIBaseSerializer<T>> serializer);

    /**
     * Gets all DataHolder types that are supported by the Web-API
     * @return A map from json key to DataHolder type
     */
    Map<String, Class> getSupportedData();

    /**
     * Converts an object directly to a json string. Includes details if specified.
     * @param obj The object to convert to json.
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json string representation of the object.
     */
    String toString(Object obj, boolean details, TreeNode<String, Boolean> perms);

    /**
     * Converts an object to json using the default object mapper. Includes details if specified.
     * @param obj The object to convert to json
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @param perms The permissions defining which properties of the object are returned.
     * @return The json representation of the object.
     */
    JsonNode toJson(Object obj, boolean details, TreeNode<String, Boolean> perms);

    /**
     * Converts a class structure to json. This includes all the fields and methods of the class
     * @param c The class for which to get the json representation.
     * @return A JsonNode representing the class.
     */
    JsonNode classToJson(Class c);
}
