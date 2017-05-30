package valandur.webapi.misc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Util {

    private static Map<Class, Field[]> fields = new HashMap<>();
    private static Map<Class, Method[]> methods = new HashMap<>();


    /**
     * Checks if the provided string is  valid UUID.
     * @param uuid The string to check.
     * @return True if the provided string is a valid UUID, false otherwise.
     */
    public static boolean isValidUUID(String uuid) {
        return uuid.split("-").length == 5;
    }

    /**
     * Gets the path parameters of a given request.
     * @param req The request from which the path parameters are extracted.
     * @return The path parameters.
     */
    public static String[] getPathParts(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return new String[] { };
        return path.substring(1).split("/");
    }

    /**
     * Gets the query parameters of a given request.
     * @param req The request from which the query parameters are extracted.
     * @return The query parameters.
     */
    public static Map<String, String> getQueryParts(HttpServletRequest req) {
        Map<String, String> map = new HashMap<>();

        String query = req.getQueryString();
        if (query == null) return map;

        String[] splits = query.split("&");
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
     * Parse a json node as an array of method parameters
     * @param node The json node that contains the information about the method parameters.
     * @return An optional which is empty on failure. On success it contains a tuple with the method parameters types and values.
     */
    public static Optional<Tuple<Class[], Object[]>> parseParams(JsonNode node) {
        if (node == null)
            return Optional.of(new Tuple<>(new Class[0], new Object[0]));

        if (!node.isArray())
            return Optional.empty();

        ArrayNode arr = (ArrayNode)node;
        Class[] paramTypes = new Class[arr.size()];
        Object[] paramValues = new Object[arr.size()];

        try {
            for (int i = 0; i < arr.size(); i++) {
                Tuple<Class, Object> tup = getParamFromJson(arr.get(i));
                paramTypes[i] = tup.getFirst();
                paramValues[i] = tup.getSecond();
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            return Optional.empty();
        }

        return Optional.of(new Tuple<>(paramTypes, paramValues));
    }
    private static Tuple<Class, Object> getParamFromJson(JsonNode node) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if (!node.isObject())
            throw new ClassNotFoundException(node.toString());

        String type = node.get("type").asText().toLowerCase();
        JsonNode e = node.get("value");

        switch (type) {
            case "int":
            case "integer":
                return new Tuple<>(Integer.class, e.asInt());
            case "float":
                return new Tuple<>(Float.class, (float)e.asDouble());
            case "double":
                return new Tuple<>(Double.class, e.asDouble());
            case "bool":
            case "boolean":
                return new Tuple<>(Boolean.class, e.asBoolean());
            case "byte":
                return new Tuple<>(Byte.class, (byte)e.asInt());
            case "char":
                return new Tuple<>(Character.class, e.asText().charAt(0));
            case "long":
                return new Tuple<>(Long.class, e.asLong());
            case "short":
                return new Tuple<>(Short.class, (short)e.asInt());
            case "string":
                return new Tuple<>(String.class, e.asText());
            case "class":
                return new Tuple<>(Class.class, Class.forName(type));
            case "enum":
                Class c = Class.forName(e.get("type").asText());
                String name = e.get("value").asText();
                return new Tuple<Class, Object>(c, Enum.valueOf(c, name));

            case "vector3d":
                return new Tuple<>(Vector3d.class, new Vector3d(e.get("x").asDouble(), e.get("y").asDouble(), e.get("z").asDouble()));

            case "vector3i":
                return new Tuple<>(Vector3i.class, new Vector3i(e.get("x").asInt(), e.get("y").asInt(), e.get("z").asInt()));

            case "text":
                return new Tuple<>(Text.class, Text.of(e.asText()));

            case "world":
                Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(e.asText()));
                return new Tuple<>(World.class, w.orElse(null));

            case "player":
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(e.asText()));
                return new Tuple<>(Player.class, p.orElse(null));

            case "itemstack":
                String cName = e.get("itemType").asText();
                Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, cName);
                int amount = e.get("amount").asInt();

                if (!t.isPresent())
                    throw new ClassNotFoundException(cName);

                return new Tuple<>(ItemStack.class, ItemStack.of(t.get(), amount));

            case "static":
                Class clazz = Class.forName(e.get("class").asText());
                Field f = clazz.getField(e.get("field").asText());
                return new Tuple<>(f.getType(), f.get(null));

            default:
                return null;
        }
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

    /**
     * Merge default values into a existing config, adding only the values that don't exist yet.
     * @param node The node that receives any missing default values
     * @param def The default configuration
     * @param restoreDefaults True if missing values in the config marked with _DEFAULT_ in the comment of the default config should be added.
     */
    public static void mergeConfigs(CommentedConfigurationNode node, CommentedConfigurationNode def, boolean restoreDefaults) {
        if (def.getComment().isPresent() && def.getComment().get().contains("_EXAMPLE_") && !restoreDefaults) {
            return;
        }

        if (node.isVirtual()) {
            node.setValue(def);
            return;
        }

        if (!def.hasMapChildren()) {
            return;
        }

        Map<Object, ? extends CommentedConfigurationNode> map = def.getChildrenMap();
        for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : map.entrySet()) {
            CommentedConfigurationNode subNode = node.getNode(entry.getKey());
            mergeConfigs(subNode, entry.getValue(), restoreDefaults);
        }
    }
}
