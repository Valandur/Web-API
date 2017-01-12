package valandur.webapi.misc;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedObject;

import java.lang.reflect.Field;
import java.util.*;

public class JsonConverter {

    public static JsonElement cacheToJson(Collection<? extends CachedObject> objects) {
        JsonArray arr = new JsonArray();
        for (CachedObject obj : objects)
            arr.add(cacheToJson(obj));
        return arr;
    }
    public static JsonElement cacheToJson(CachedObject obj) {
        return cacheToJson(obj, false);
    }
    public static JsonElement cacheToJson(CachedObject obj, boolean details){
        GsonBuilder builder = new GsonBuilder();
        if (!details) builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        return gson.toJsonTree(obj);
    }

    public static JsonElement tileEntityToJson(TileEntity te) {
        if (te == null)
            return JsonNull.INSTANCE;

        JsonElement json = toJson(te);

        if (json instanceof JsonObject) {
            JsonObject obj = (JsonObject)json;

            obj.add("location", toJson(te.getLocation()));
            obj.add("data", containerToJson(te));
            obj.add("properties", propertiesToJson(te));

            if (te instanceof TileEntityCarrier) {
                Inventory inventory = ((TileEntityCarrier) te).getInventory();
                obj.add("inventory", toJson(inventory));
            }
        }

        return json;
    }

    private static JsonObject containerToJson(ValueContainer container) {
        JsonObject obj = new JsonObject();

        if (container instanceof CompositeValueStore) {
            CompositeValueStore store = (CompositeValueStore)container;
            if (store.supports(FoodData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(FoodData.class).get();
                obj = containerToJson(subCont);
            }
        }

        Set<ImmutableValue<?>> values = container.getValues();
        for (ImmutableValue<?> immutable : values) {
            String key = Util.lowerFirst(immutable.getKey().getQuery().asString("."));
            Object val = immutable.get();

            if (val instanceof ImmutableSet) {
                JsonArray subArr = new JsonArray();
                for (Object o : ((ImmutableSet)val)) {
                    subArr.add(toJson(o));
                }
                obj.add(key, subArr);
            } else if (val instanceof ImmutableList) {
                JsonArray subArr = new JsonArray();
                for (Object o : ((ImmutableList)val)) {
                    subArr.add(toJson(o));
                }
                obj.add(key, subArr);
            } else {
                obj.add(key, toJson(val));
            }
        }
        return obj;
    }
    private static JsonObject propertiesToJson(PropertyHolder holder) {
        JsonObject obj = new JsonObject();
        Collection<Property<?, ?>> properties = holder.getApplicableProperties();
        for (Property<?, ?> prop : properties) {
            obj.add(Util.lowerFirst(prop.getKey().toString()), toJson(prop.getValue()));
        }
        return obj;
    }

    public static JsonElement toJson(Object obj) {
        return toJson(obj, new IdentityHashMap<>());
    }
    private static JsonElement toJson(Object obj, Map<Object, JsonObject> seen) {
        if (obj == null)
            return JsonNull.INSTANCE;
        else if (obj instanceof Number)
            return new JsonPrimitive((Number)obj);
        else if (obj instanceof Boolean)
            return new JsonPrimitive((Boolean)obj);
        else if (obj instanceof Enum<?> || obj instanceof String)
            return new JsonPrimitive(obj.toString());
        else if (obj instanceof Vector3i)
            return toJson((Vector3i)obj);
        else if (obj instanceof Vector3d)
            return toJson((Vector3d)obj);
        else if (obj instanceof Location<?>)
            return toJson((Location<World>)obj);
        else if (obj instanceof Inventory)
            return toJson((Inventory)obj);
        else if (obj instanceof ItemStack)
            return toJson((ItemStack)obj);

        else if (obj instanceof Collection<?>)
            return toJson((Collection<?>)obj, seen);
        else if (obj instanceof Map<?,?>)
            return toJson((Map<?,?>)obj, seen);
        else if (obj.getClass().isArray())
            return toJson((Object[])obj, seen);
        else
            return objectToJson(obj, seen);
    }

    private static JsonElement objectToJson(Object obj, Map<Object, JsonObject> seen) {
        JsonObject json = new JsonObject();
        seen.put(obj, json);

        for (Field f : obj.getClass().getFields()) {
            if (f.getName().startsWith("field_"))
                continue;

            try {
                Object val = f.get(obj);
                if (seen.containsKey(val)) {
                    JsonObject seenObj = seen.get(val);
                    if (!seenObj.has("@id"))
                        seenObj.addProperty("@id", System.identityHashCode(val));
                    json.addProperty(f.getName(), "id@" + System.identityHashCode(val));
                    continue;
                }

                json.add(f.getName(), toJson(val, seen));
            } catch (Exception e) {
                json.add(f.getName(), JsonNull.INSTANCE);
            }
        }

        seen.remove(obj);
        return json;
    }
    public static JsonElement toJson(Object[] list, Map<Object, JsonObject> seen) {
        JsonArray arr = new JsonArray();

        for (Object e : list) {
            arr.add(toJson(e, seen));
        }

        return arr;
    }
    public static JsonElement toJson(Collection<?> coll, Map<Object, JsonObject> seen) {
        JsonArray arr = new JsonArray();

        for (Object e : coll) {
            arr.add(toJson(e, seen));
        }

        return arr;
    }
    public static JsonElement toJson(Map<?, ?> map, Map<Object, JsonObject> seen) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<?, ?> e : map.entrySet()) {
            obj.add(e.getKey().toString(), toJson(e.getValue(), seen));
        }

        return obj;
    }

    public static JsonElement toJson(World world) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", world.getName());
        obj.addProperty("uuid", world.getUniqueId().toString());
        return obj;
    }
    public static JsonElement toJson(Vector3i vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getX());
        obj.addProperty("z", vector.getX());
        return obj;
    }
    public static JsonElement toJson(Vector3d vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getY());
        obj.addProperty("z", vector.getZ());
        return obj;
    }
    public static JsonElement toJson(Location<World> location) {
        JsonObject obj = new JsonObject();
        obj.add("world", toJson(location.getExtent()));
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        return obj;
    }
    public static JsonElement toJson(Inventory inventory) {
        JsonArray arr = new JsonArray();
        for (Inventory inv : inventory.slots()) {
            Optional<ItemStack> stack = inv.peek();
            if (stack.isPresent()) {
                arr.add(toJson(stack.get()));
            }
        }
        return arr;
    }
    public static JsonElement toJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", stack.getItem().getId());
        obj.addProperty("name", stack.getTranslation().get());
        obj.addProperty("quantity", stack.getQuantity());
        return obj;
    }
}
