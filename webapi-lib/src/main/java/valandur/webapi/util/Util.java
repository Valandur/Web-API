package valandur.webapi.util;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class Util {

    private static Map<Class, Field[]> fields = new HashMap<>();
    private static Map<Class, Method[]> methods = new HashMap<>();

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a random id from a secure number generator
     * @return A random unique id
     */
    public static String generateUniqueId() {
        return new BigInteger(130, random).toString(32);
    }

    /**
     * Checks if the provided string is  valid UUID.
     * @param uuid The string to check.
     * @return True if the provided string is a valid UUID, false otherwise.
     */
    public static boolean isValidUUID(String uuid) {
        return uuid != null && uuid.split("-").length == 5;
    }

    /**
     * Gets the query parameters of a given request.
     * @param req The request from which the query parameters are extracted.
     * @return The query parameters.
     */
    public static Map<String, String> getQueryParams(HttpServletRequest req) {
        Map<String, String> map = new HashMap<>();

        String query = req.getQueryString();
        if (query == null) return map;

        String[] splits = query.split("\\&");
        for (String split : splits) {
            String[] subSplits = split.split("=");
            map.put(subSplits[0], subSplits.length == 2 ? subSplits[1] : "");
        }
        return map;
    }

    /**
     * Transform the first letter to lowercase.
     * @param text The text to transform.
     * @return The transformed text.
     */
    public static String lowerFirst(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Returns all NON MINECRAFT NATIVE fields from a class and it's ancestors. (The fields that don't start with "field_")
     * @param c The class for which to get the fields.
     * @return The array of fields of that class and all inherited fields.
     */
    public static Field[] getAllFields(Class c) {
        if (fields.containsKey(c))
            return fields.get(c);

        List<Field> fs = new LinkedList<>();
        while (c != null) {
            fs.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }

        fields.put(c, fs.stream().filter(f -> !f.getName().startsWith("field_")).toArray(Field[]::new));
        return fields.get(c);
    }

    /**
     * Returns all NON MINECRAFT NATIVE methods from a class and it's ancestors. (The methods that don't start with "func_")
     * @param c The class for which to get the methods
     * @return The array of methods of that class and all inherited methods.
     */
    public static Method[] getAllMethods(Class c) {
        if (methods.containsKey(c))
            return methods.get(c);

        List<Method> ms = new LinkedList<>();
        while (c != null) {
            ms.addAll(Arrays.asList(c.getDeclaredMethods()));
            c = c.getSuperclass();
        }

        methods.put(c, ms.stream().filter(m -> !m.getName().startsWith("func_")).toArray(Method[]::new));
        return methods.get(c);
    }
}
