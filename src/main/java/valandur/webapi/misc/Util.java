package valandur.webapi.misc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class Util {

    public static String[] getPathParts(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return new String[] { };
        return path.substring(1).split("/");
    }

    public static Map<String, String> getQueryParts(HttpServletRequest req) {
        Map<String, String> map = new HashMap<>();
        String[] splits = req.getQueryString().split("&");
        for (String split : splits) {
            String[] subSplits = split.split("=");
            map.put(subSplits[0], subSplits[1]);
        }
        return map;
    }

    public static String lowerFirst(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

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
                Tuple<Class, Object> def = getParamFromJson(arr.get(i));
                paramTypes[i] = def.getFirst();
                paramValues[i] = def.getSecond();
            }
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }

        return Optional.of(new Tuple<>(paramTypes, paramValues));
    }

    private static Tuple<Class, Object> getParamFromJson(JsonNode node) throws ClassNotFoundException {
        if (!node.isObject())
            return new Tuple<>(null, null);

        String type = node.get("type").asText().toLowerCase();
        JsonNode e = node.get("value");

        switch (type) {
            case "int":
            case "integer":
                return new Tuple<>(int.class, e.asInt());
            case "float":
                return new Tuple<>(float.class, (float)e.asDouble());
            case "double":
                return new Tuple<>(double.class, e.asDouble());
            case "bool":
            case "boolean":
                return new Tuple<>(boolean.class, e.asBoolean());
            case "byte":
                return new Tuple<>(byte.class, (byte)e.asInt());
            case "char":
                return new Tuple<>(char.class, e.asText().charAt(0));
            case "long":
                return new Tuple<>(long.class, e.asLong());
            case "short":
                return new Tuple<>(short.class, (short)e.asInt());
            case "string":
                return new Tuple<>(String.class, e.asText());
            case "class":
                return new Tuple<>(Class.class, Class.forName(type));

            case "vector3d":
                return new Tuple<>(Vector3d.class, new Vector3d(e.get("x").asDouble(), e.get("y").asDouble(), e.get("z").asDouble()));

            case "text":
                return new Tuple<>(Text.class, Text.of(e.asText()));

            case "world":
                Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(e.asText()));
                return new Tuple<>(World.class, w.orElse(null));

            case "player":
                Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(e.asText()));
                return new Tuple<>(World.class, p.orElse(null));

            case "itemstack":
                String cName = e.get("itemType").asText();
                Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, cName);
                int amount = e.get("amount").asInt();

                if (!t.isPresent())
                    throw new ClassNotFoundException(cName);

                return new Tuple<>(ItemStack.class, ItemStack.of(t.get(), amount));

            default:
                return new Tuple<>(Class.forName(type), null);
        }
    }
}
