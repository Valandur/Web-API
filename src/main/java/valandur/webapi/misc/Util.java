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
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Util {

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
    public static Optional<Object[]> parseParams(JsonNode node) {
        if (node == null)
            return Optional.of(new Object[0]);

        if (!node.isArray())
            return Optional.empty();

        ArrayNode arr = (ArrayNode)node;
        Object[] paramValues = new Object[arr.size()];

        try {
            for (int i = 0; i < arr.size(); i++) {
                paramValues[i] = getParamFromJson(arr.get(i));
            }
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }

        return Optional.of(paramValues);
    }
    private static Object getParamFromJson(JsonNode node) throws ClassNotFoundException {
        if (!node.isObject())
            throw new ClassNotFoundException(node.toString());

        String type = node.get("type").asText().toLowerCase();
        JsonNode e = node.get("value");

        switch (type) {
            case "int":
            case "integer":
                return e.asInt();
            case "float":
                return (float)e.asDouble();
            case "double":
                return e.asDouble();
            case "bool":
            case "boolean":
                return e.asBoolean();
            case "byte":
                return (byte)e.asInt();
            case "char":
                return e.asText().charAt(0);
            case "long":
                return e.asLong();
            case "short":
                return (short)e.asInt();
            case "string":
                return e.asText();
            case "class":
                return Class.forName(type);
            case "enum":
                Class c = Class.forName(e.get("type").asText());
                String name = e.get("value").asText();
                return Enum.valueOf(c, name);

            case "vector3d":
                return new Vector3d(e.get("x").asDouble(), e.get("y").asDouble(), e.get("z").asDouble());

            case "vector3i":
                return new Vector3i(e.get("x").asInt(), e.get("y").asInt(), e.get("z").asInt());

            case "text":
                return Text.of(e.asText());

            case "world":
                Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(e.asText()));
                return w.orElse(null);

            case "player":
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(e.asText()));
                return p.orElse(null);

            case "itemstack":
                String cName = e.get("itemType").asText();
                Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, cName);
                int amount = e.get("amount").asInt();

                if (!t.isPresent())
                    throw new ClassNotFoundException(cName);

                return ItemStack.of(t.get(), amount);

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
        List<Field> fs = new LinkedList<>();
        while (c != null) {
            fs.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        return fs.stream().filter(f -> !f.getName().startsWith("field_")).toArray(Field[]::new);
    }

    /**
     * Returns all NON MINECRAFT NATIVE methods from a class and it's ancestors. (The methods that don't start with "func_")
     * @param c The class for which to get the methods
     * @return The array of methods of that class and all inherited methods.
     */
    public static Method[] getAllMethods(Class c) {
        List<Method> ms = new LinkedList<>();
        while (c != null) {
            ms.addAll(Arrays.asList(c.getDeclaredMethods()));
            c = c.getSuperclass();
        }
        return ms.stream().filter(m -> !m.getName().startsWith("func_")).toArray(Method[]::new);
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
