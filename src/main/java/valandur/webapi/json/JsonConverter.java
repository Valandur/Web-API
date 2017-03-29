package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class JsonConverter {

    public static String toString(Object obj) {
        return toString(obj, false);
    }

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
     * Converts an object to json using the default object mapper without details.
     * @param obj The object to convert to json.
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

        addSerializer(om, Achievement.class, new AchievementSerializer());
        addSerializer(om, Ban.Profile.class, new BanSerializer());
        addSerializer(om, Cause.class, new CauseSerializer());
        addSerializer(om, DataContainer.class, new DataContainerSerializer());
        addSerializer(om, Dimension.class, new DimensionSerializer());
        addSerializer(om, DimensionType.class, new DimensionTypeSerializer());
        addSerializer(om, Entity.class, new EntitySerializer());
        addSerializer(om, GameProfile.class, new GameProfileSerializer());
        addSerializer(om, GeneratorType.class, new GeneratorTypeSerializer());
        addSerializer(om, Map.class, new MapSerializer());
        addSerializer(om, Player.class, new PlayerSerializer());
        addSerializer(om, Property.class, new PropertySerializer());
        addSerializer(om, TileEntity.class, new TileEntitySerializer());
        //addSerializer(om, User.class, new UserSerializer());
        addSerializer(om, Vector3d.class, new VectorSerializer());
        addSerializer(om, World.class, new WorldSerializer());

        return om;
    }
    private static void addSerializer(ObjectMapper mapper, Class attachClass, JsonSerializer serializer) {
        SimpleModule mod = new SimpleModule();
        mod.addSerializer(attachClass, serializer);
        mapper.registerModule(mod);
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
