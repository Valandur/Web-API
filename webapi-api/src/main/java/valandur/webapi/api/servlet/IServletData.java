package valandur.webapi.api.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import valandur.webapi.api.annotation.WebAPIEndpoint;

import java.util.Optional;

/**
 * This class bundles the data that is received and sent from servlets when a client accesses an endpoint.
 */
public interface IServletData {

    /**
     * Gets the object node which is returned as the json response.
     * If you want to add data to the response, please use the {@link #addJson(String, Object, boolean)} method
     * @return The object node that will be transformed to json.
     */
    ObjectNode getNode();

    /**
     * Gets the last parse error that occurred when trying to convert the request body to a class using the
     * {@link #getRequestBody(Class)} method.
     * @return The exception that occurred.
     */
    Exception getLastParseError();

    /**
     * True if this response was already marked as an error, and the error response was already sent to the client,
     * false otherwise.
     * @return True if the error response was already sent to the client, false otherwise.
     */
    boolean isErrorSent();

    /**
     * Gets the request body that was sent by the client. Empty if this is a GET request or the client didn't send
     * any data.
     * @return The request data sent by the client.
     */
    JsonNode getRequestBody();

    /**
     * Gets the request body that was sent by the client, parsed to the specified class. This is a convenience method
     * to simplify parsing properties. If parsing fails because the data isn't formatted properly this returns
     * and empty optional and {@link #getLastParseError()} contains the error message that occurred.
     * @param clazz The class that the request body is parsed to.
     * @param <T> The type of the class.
     * @return An optional containing the parsed request body, or empty if the parse failed.
     */
    <T> Optional<T> getRequestBody(Class<T> clazz);

    /**
     * Adds a header to the response.
     * @param name The name of the header.
     * @param value The value of the header.
     */
    void setHeader(String name, String value);

    /**
     * Sets the response status to the specified code. Check {@link javax.servlet.http.HttpServletResponse} for
     * various response codes.
     * @param status The new status.
     */
    void setStatus(int status);

    /**
     * Adds an object to the json response data.
     * @param key The key under which the object is added.
     * @param value The object which is added to the response.
     * @param details True if details of the object should be included.
     */
    void addJson(String key, Object value, boolean details);

    /**
     * Gets the path parameter with the specified key. The path parameters are parsed according to the
     * {@link WebAPIEndpoint} annotation.
     * @param key The key of the parameter.
     * @return The path parameter, or null if the key is invalid.
     */
    String getPathParam(String key);

    /**
     * Gets the specified property from the query string.
     * @param key The key of the property.
     * @return An optional containing the property if it was present.
     */
    Optional<String> getQueryParam(String key);

    /**
     * Sends the specified error response code and message to the client. See
     * {@link javax.servlet.http.HttpServletResponse} for various response codes.
     * Note that after sending an error response further operations on this response will yield unexpected results.
     * @param error The error code which is sent to the client.
     * @param message The message describing what went wrong.
     */
    void sendError(int error, String message);
}
