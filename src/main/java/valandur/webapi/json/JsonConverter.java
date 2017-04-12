package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.*;
import valandur.webapi.json.serializers.entity.EntitySerializer;
import valandur.webapi.json.serializers.entity.HealthDataSerializer;
import valandur.webapi.json.serializers.events.CauseSerializer;
import valandur.webapi.json.serializers.events.EventSerializer;
import valandur.webapi.json.serializers.general.*;
import valandur.webapi.json.serializers.player.*;
import valandur.webapi.json.serializers.tileentity.TileEntitySerializer;
import valandur.webapi.json.serializers.world.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class JsonConverter {

    private static Map<Class, JsonSerializer> defaultSerializers;
    static {
        Map<Class, JsonSerializer> serializers = new HashMap<>();

        // Entity
        serializers.put(Entity.class, new EntitySerializer());
        serializers.put(HealthData.class, new HealthDataSerializer());

        // Player
        serializers.put(Achievement.class, new AchievementSerializer());
        serializers.put(Ban.Profile.class, new BanSerializer());
        serializers.put(FoodData.class, new FoodDataSerializer());
        serializers.put(GameProfile.class, new GameProfileSerializer());
        serializers.put(Player.class, new PlayerSerializer());

        // World
        serializers.put(Dimension.class, new DimensionSerializer());
        serializers.put(DimensionType.class, new DimensionTypeSerializer());
        serializers.put(GeneratorType.class, new GeneratorTypeSerializer());
        serializers.put(World.class, new WorldSerializer());
        serializers.put(WorldBorder.class, new WorldBorderSerializer());

        // Tile-Entity
        serializers.put(TileEntity.class, new TileEntitySerializer());

        // General
        serializers.put(ItemStack.class, new ItemStackSerializer());
        serializers.put(Inventory.class, new InventorySerializer());
        serializers.put(Cause.class, new CauseSerializer());
        serializers.put(Map.class, new MapSerializer());
        serializers.put(Vector3d.class, new VectorSerializer());
        serializers.put(Event.class, new EventSerializer());

        defaultSerializers = serializers;
    }

    /**
     * Converts an object directly to a json string. EXCLUDES details.
     * @param obj The object to convert to json.
     * @return The json string representation of the object.
     */
    public static String toString(Object obj) {
        return toString(obj, false);
    }

    /**
     * Converts an object directly to a json string. Includes details if specified.
     * @param obj The object to convert to json.
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json string representation of the object.
     */
    public static String toString(Object obj, boolean details) {
        ObjectMapper om = getDefaultObjectMapper();

        if (!details) {
            om.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
        }

        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Converts an object to json using the default object mapper. EXCLUDES details.
     * @param obj The object to convert to json
     * @return The json representation of the object.
     */
    public static JsonNode toJson(Object obj) {
        return toJson(obj, false);
    }

    /**
     * Converts an object to json using the default object mapper. Includes details if specified.
     * @param obj The object to convert to json
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json representation of the object.
     */
    public static JsonNode toJson(Object obj, boolean details) {
        ObjectMapper om = getDefaultObjectMapper();
        if (!details) {
            om.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
        }
        return om.valueToTree(obj);
    }

    /**
     * Get the default object mapper which contains some custom serializers and doesn't fail on empty beans.
     * .@return The default object mapper
     */
    public static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        for (Map.Entry<Class, JsonSerializer> entry : defaultSerializers.entrySet()) {
            SimpleModule mod = new SimpleModule();
            mod.addSerializer(entry.getKey(), entry.getValue());
            om.registerModule(mod);
        }

        return om;
    }


    /**
     * Converts a class structure to json. This includes all the fields and methods of the class
     * @param c The class for which to get the json representation.
     * @return A JsonNode representing the class.
     */
    public static JsonNode classToJson(Class c) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put("name", c.getName());
        json.put("parent", c.getSuperclass().getName());

        ObjectNode jsonFields = JsonNodeFactory.instance.objectNode();
        Field[] fs = getAllFields(c);
        for (Field f : fs) {
            ObjectNode jsonField = JsonNodeFactory.instance.objectNode();

            f.setAccessible(true);

            jsonField.put("type", f.getType().getName());

            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            int mod = f.getModifiers();
            if (Modifier.isAbstract(mod)) arr.add("abstract");
            if (Modifier.isFinal(mod)) arr.add("final");
            if (Modifier.isInterface(mod)) arr.add("interface");
            if (Modifier.isNative(mod)) arr.add("native");
            if (Modifier.isPrivate(mod)) arr.add("private");
            if (Modifier.isProtected(mod)) arr.add("protected");
            if (Modifier.isPublic(mod)) arr.add("public");
            if (Modifier.isStatic(mod)) arr.add("static");
            if (Modifier.isStrict(mod)) arr.add("strict");
            if (Modifier.isSynchronized(mod)) arr.add("synchronized");
            if (Modifier.isTransient(mod)) arr.add("transient");
            if (Modifier.isVolatile(mod)) arr.add("volatile");
            jsonField.set("modifiers", arr);

            if (f.getDeclaringClass() != c) {
                jsonField.put("from", f.getDeclaringClass().getName());
            }

            jsonFields.set(f.getName(), jsonField);
        }
        json.set("fields", jsonFields);

        ObjectNode jsonMethods = JsonNodeFactory.instance.objectNode();
        Method[] ms = getAllMethods(c);
        for (Method m : ms) {
            ObjectNode jsonMethod = JsonNodeFactory.instance.objectNode();

            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            int mod = m.getModifiers();
            if (Modifier.isAbstract(mod)) arr.add("abstract");
            if (Modifier.isFinal(mod)) arr.add("final");
            if (Modifier.isInterface(mod)) arr.add("interface");
            if (Modifier.isNative(mod)) arr.add("native");
            if (Modifier.isPrivate(mod)) arr.add("private");
            if (Modifier.isProtected(mod)) arr.add("protected");
            if (Modifier.isPublic(mod)) arr.add("public");
            if (Modifier.isStatic(mod)) arr.add("static");
            if (Modifier.isStrict(mod)) arr.add("strict");
            if (Modifier.isSynchronized(mod)) arr.add("synchronized");
            if (Modifier.isTransient(mod)) arr.add("transient");
            if (Modifier.isVolatile(mod)) arr.add("volatile");
            jsonMethod.set("modifiers", arr);

            ArrayNode arr2 = JsonNodeFactory.instance.arrayNode();
            for (Parameter p : m.getParameters()) {
                arr2.add(p.getType().getName());
            }
            jsonMethod.set("params", arr2);

            jsonMethod.put("return", m.getReturnType().getName());

            if (m.getDeclaringClass() != c) {
                jsonMethod.put("from", m.getDeclaringClass().getName());
            }

            jsonMethods.set(m.getName(), jsonMethod);
        }
        json.set("methods", jsonMethods);

        return json;
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
}
