package valandur.webapi.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowpowered.math.vector.Vector3d;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class JsonConverter {

    public static List<String> hiddenClasses = new ArrayList<String>();

    public static JsonNode toJson(Object obj) {
        return toJson(obj, false);
    }
    public static JsonNode toJson(Object obj, boolean details) {
        ObjectMapper om = new ObjectMapper();

        addSerializer(om, Vector3d.class, new VectorSerializer());
        addSerializer(om, Property.class, new PropertySerializer());
        addSerializer(om, Map.class, new MapSerializer());
        addSerializer(om, DataContainer.class, new DataContainerSerializer());

        if (!details) {
            om.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
            om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }

        return om.valueToTree(obj);
    }

    private static void addSerializer(ObjectMapper mapper, Class attachClass, JsonSerializer serializer) {
        SimpleModule mod = new SimpleModule();
        mod.addSerializer(attachClass, serializer);
        mapper.registerModule(mod);
    }


    /*
    // Convert general elements
    public static JsonElement toRawJson(Object obj) {
        return toRawJson(obj, new IdentityHashMap<>(), true);
    }
    private static JsonElement toRawJson(Object obj, Map<Object, JsonObject> seen) {
        return toRawJson(obj, seen, false);
    }
    private static JsonElement toRawJson(Object obj, Map<Object, JsonObject> seen, boolean isRoot) {
        if (obj == null)
            return JsonNull.INSTANCE;

        String cName = obj.getClass().getName();

        if (hiddenClasses.contains(cName.toLowerCase()))
            return JsonNull.INSTANCE;
        else if (obj instanceof Number)
            return new JsonPrimitive((Number)obj);
        else if (obj instanceof Boolean)
            return new JsonPrimitive((Boolean)obj);
        else if (obj instanceof Enum<?> || obj instanceof String)
            return new JsonPrimitive(obj.toString());
        else if (obj instanceof Text)
            return new JsonPrimitive(((Text)obj).toPlain());
        else if (obj instanceof Instant)
            return new JsonPrimitive(((Instant)obj).getEpochSecond());
        else if (obj instanceof Class)
            return new JsonPrimitive(((Class)obj).getName());

        else if (obj instanceof Vector3i)
            return vectorToJson((Vector3i)obj);
        else if (obj instanceof Vector3d)
            return vectorToJson((Vector3d)obj);
        else if (obj instanceof Location<?>)
            return locationToJson((Location<World>)obj);
        else if (obj instanceof ItemStack)
            return itemStackToJson((ItemStack)obj);

        else if (obj instanceof Optional)
            return optionalToJson((Optional)obj, seen, isRoot);

        else if (obj instanceof Collection<?>)
            return collectionToJson((Collection<?>)obj, seen);
        else if (obj instanceof Map<?,?>)
            return mapToJson((Map<?,?>)obj, seen);
        else if (obj.getClass().isArray())
            return listToJson((Object[])obj, seen);

        else
            return objectToJson(obj, seen, isRoot);
    }

    private static JsonElement optionalToJson(Optional o, Map<Object, JsonObject> seen, boolean isRoot) {
        JsonObject obj = new JsonObject();
        obj.addProperty("present", o.isPresent());
        obj.add("value", o.isPresent() ? toRawJson(o.get(), seen, isRoot) : null);
        return obj;
    }

    private static JsonObject objectToJson(Object obj, Map<Object, JsonObject> seen, boolean isRoot) {
        JsonObject json = new JsonObject();
        seen.put(obj, json);

        String name = obj.getClass().getName();
        json.addProperty("class", name);

        // Add all the fields of the class
        Field[] fs = isRoot ? getAllFields(obj.getClass()) : obj.getClass().getDeclaredFields();
        for (Field f : fs) {
            if (f.getName().startsWith("field_"))
                continue;

            f.setAccessible(true);

            try {
                Object val = f.get(obj);
                if (seen.containsKey(val)) {
                    JsonObject seenObj = seen.get(val);
                    if (!seenObj.has("@id"))
                        seenObj.addProperty("@id", System.identityHashCode(val));
                    json.addProperty(f.getName(), "id@" + System.identityHashCode(val));
                    continue;
                }

                json.add(f.getName(), toRawJson(val, seen));
            } catch (Exception e) {
                json.add(f.getName(), new JsonPrimitive(e.toString()));
            }
        }

        // Additional data & properties
        if (obj instanceof ValueContainer)
            json.add("data", containerToJson((ValueContainer) obj));
        else if (obj instanceof DataContainer)
            json.add("data", containerToJson((DataContainer) obj));

        if (obj instanceof PropertyHolder)
            json.add("properties", propertiesToJson((PropertyHolder) obj));
        else if (obj instanceof World)
            json.add("properties", containerToJson(((World) obj).getProperties().toContainer()));

        if (obj instanceof Inventory) {
            json.add("inventory", inventoryToJson((Inventory)obj));
        }

        seen.remove(obj);
        return json;
    }
    private static JsonElement listToJson(Object[] list, Map<Object, JsonObject> seen) {
        JsonArray arr = new JsonArray();

        for (Object e : list) {
            if (seen.containsKey(e)) {
                JsonObject seenObj = seen.get(e);
                if (!seenObj.has("@id"))
                    seenObj.addProperty("@id", System.identityHashCode(e));
                arr.add(new JsonPrimitive("id@" + System.identityHashCode(e)));
                continue;
            }

            arr.add(toRawJson(e, seen));
        }

        return arr;
    }
    private static JsonElement collectionToJson(Collection<?> coll, Map<Object, JsonObject> seen) {
        JsonArray arr = new JsonArray();

        for (Object e : coll) {
            if (seen.containsKey(e)) {
                JsonObject seenObj = seen.get(e);
                if (!seenObj.has("@id"))
                    seenObj.addProperty("@id", System.identityHashCode(e));
                arr.add(new JsonPrimitive("id@" + System.identityHashCode(e)));
                continue;
            }

            arr.add(toRawJson(e, seen));
        }

        return arr;
    }
    private static JsonElement mapToJson(Map<?, ?> map, Map<Object, JsonObject> seen) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (seen.containsKey(e)) {
                JsonObject seenObj = seen.get(e);
                if (!seenObj.has("@id"))
                    seenObj.addProperty("@id", System.identityHashCode(e));
                obj.addProperty(e.getKey().toString(), "id@" + System.identityHashCode(e));
                continue;
            }

            obj.add(e.getKey().toString(), toRawJson(e.getValue(), seen));
        }

        return obj;
    }

    public static JsonElement inventoryToJson(Inventory inventory) {
        Collection<Object> objs = new ArrayList<>();
        inventory.slots().forEach(i -> { if (i.peek().isPresent()) objs.add(i.peek().get()); });
        return JsonConverter.toRawJson(objs);
    }
    public static JsonElement containerToJson(ValueContainer container) {
        JsonObject obj = new JsonObject();

        if (container instanceof CompositeValueStore) {
            CompositeValueStore store = (CompositeValueStore)container;
            if (store.supports(FoodData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(FoodData.class).get();
                obj = (JsonObject)containerToJson(subCont);
            }
        }

        Set<ImmutableValue<?>> values = container.getValues();
        for (ImmutableValue<?> immutable : values) {
            String key = Util.lowerFirst(immutable.getKey().getQuery().asString("."));
            Object val = immutable.get();

            if (val instanceof ImmutableSet) {
                JsonArray subArr = new JsonArray();
                for (Object o : ((ImmutableSet)val)) {
                    subArr.add(toRawJson(o));
                }
                obj.add(key, subArr);
            } else if (val instanceof ImmutableList) {
                JsonArray subArr = new JsonArray();
                for (Object o : ((ImmutableList)val)) {
                    subArr.add(toRawJson(o));
                }
                obj.add(key, subArr);
            } else {
                obj.add(key, toRawJson(val));
            }
        }
        return obj;
    }
    public static JsonElement containerToJson(DataContainer container) {
        JsonObject obj = new JsonObject();

        Map<DataQuery, Object> data = container.getValues(false);
        for (Map.Entry<DataQuery, Object> entry : data.entrySet()) {
            obj.add(Util.lowerFirst(entry.getKey().asString("-")), toRawJson(entry.getValue()));
        }

        return obj;
    }
    public static JsonElement propertiesToJson(PropertyHolder holder) {
        JsonObject obj = new JsonObject();
        Collection<Property<?, ?>> properties = holder.getApplicableProperties();
        for (Property<?, ?> prop : properties) {
            obj.add(Util.lowerFirst(prop.getKey().toString()), toRawJson(prop.getValue()));
        }
        return obj;
    }

    private static JsonElement vectorToJson(Vector3i vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getX());
        obj.addProperty("z", vector.getX());
        return obj;
    }
    private static JsonElement vectorToJson(Vector3d vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getY());
        obj.addProperty("z", vector.getZ());
        return obj;
    }
    private static JsonElement locationToJson(Location<World> location) {
        JsonObject obj = new JsonObject();
        obj.add("world", worldToJson(location.getExtent()));
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        return obj;
    }
    private static JsonElement worldToJson(World world) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", world.getName());
        obj.addProperty("uuid", world.getUniqueId().toString());
        return obj;
    }
    private static JsonElement itemStackToJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", stack.getItem().getId());
        obj.addProperty("name", stack.getTranslation().get());
        obj.addProperty("quantity", stack.getQuantity());
        return obj;
    }
    */

    // Convert class overview
    public static JsonNode classToJson(Class c) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        ObjectNode jsonFields = JsonNodeFactory.instance.objectNode();
        Field[] fs = Arrays.stream(getAllFields(c)).filter(m -> !m.getName().startsWith("field_")).toArray(Field[]::new);
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
        Method[] ms = Arrays.stream(getAllMethods(c)).filter(m -> !m.getName().startsWith("func_")).toArray(Method[]::new);
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
    public static Field[] getAllFields(Class c) {
        Field[] fs = new Field[]{};
        while (c != null) {
            fs = ArrayUtils.addAll(c.getDeclaredFields(), fs);
            c = c.getSuperclass();
        }
        return fs;
    }
    public static Method[] getAllMethods(Class c) {
        Method[] ms = new Method[]{};
        while (c != null) {
            ms = ArrayUtils.addAll(c.getDeclaredMethods(), ms);
            c = c.getSuperclass();
        }
        return ms;
    }
}
