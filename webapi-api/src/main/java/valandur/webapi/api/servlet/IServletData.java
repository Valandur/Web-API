package valandur.webapi.api.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public interface IServletData {

    ObjectNode getNode();

    Exception getLastParseError();

    boolean isErrorSent();

    JsonNode getRequestBody();

    <T> Optional<T> getRequestBody(Class<T> clazz);

    void setHeader(String name, String value);

    void setStatus(int status);

    void addJson(String key, Object value, boolean details);

    String getPathParam(String key);

    Optional<String> getQueryParam(String key);

    void sendError(int error, String message);
}
