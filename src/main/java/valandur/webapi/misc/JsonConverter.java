package valandur.webapi.misc;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.WebAPI;

import java.util.*;

public class JsonConverter {

    public static JsonObject toJson(PluginContainer plugin) {
        return toJson(plugin, false);
    }
    public static JsonObject toJson(PluginContainer plugin, boolean details) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", plugin.getId());
        obj.addProperty("name", plugin.getName());

        if (details) {
            obj.addProperty("description", Util.getOptStringOrNull(plugin.getDescription()));
            obj.addProperty("version", Util.getOptStringOrNull(plugin.getVersion()));
            obj.addProperty("url", Util.getOptStringOrNull(plugin.getUrl()));

            JsonArray arr = new JsonArray();
            for (String author : plugin.getAuthors()) {
                arr.add(new JsonPrimitive(author));
            }
            obj.add("authors", arr);
        }
        return obj;
    }

    public static JsonObject toJson(Player player) {
        return toJson(player, false);
    }
    public static JsonObject toJson(Player player, boolean details) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", player.getName());
        obj.addProperty("uuid", player.getUniqueId().toString());

        WebAPI.getInstance().getLogger().info("c: " + player.getContainers().size());

        if (details) {
            obj.add("position", toJson(player.getLocation()));
            obj.add("velocity", toJson(player.getVelocity()));
            obj.add("rotation", toJson(player.getRotation()));
            obj.addProperty("address", player.getConnection().getAddress().toString());
            obj.add("data", containerToJson(player));
            obj.add("properties", propertiesToJson(player));
        }

        return obj;
    }

    public static JsonObject toJson(World world) {
        return toJson(world, false);
    }
    public static JsonObject toJson(World world, boolean details) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", world.getName());
        obj.addProperty("uuid", world.getUniqueId().toString());

        if (details) {
            obj.addProperty("time", world.getProperties().getWorldTime());
            obj.addProperty("difficulty", world.getDifficulty().getName());
            obj.add("spawn", toJson(world.getProperties().getSpawnPosition()));

            JsonArray chunks = new JsonArray();
            for (Chunk chunk : world.getLoadedChunks()) {
                JsonObject jsonChunk = new JsonObject();
                jsonChunk.add("position", toJson(chunk.getPosition()));
                jsonChunk.addProperty("entities", chunk.getEntities().size());
                chunks.add(jsonChunk);
            }
            obj.add("loadedChunks", chunks);

            JsonObject gameRules = new JsonObject();
            for (Map.Entry<String, String> rule : world.getGameRules().entrySet()) {
                gameRules.addProperty(rule.getKey(), rule.getValue());
            }
            obj.add("gameRules", gameRules);

            WorldBorder border = world.getWorldBorder();
            JsonObject jsonBorder = new JsonObject();
            jsonBorder.add("center", toJson(border.getCenter()));
            jsonBorder.addProperty("diameter", border.getDiameter());
            jsonBorder.addProperty("damageAmount", border.getDamageAmount());
            jsonBorder.addProperty("damageThreshold", border.getDamageThreshold());
            jsonBorder.addProperty("warningDistance", border.getWarningDistance());
            jsonBorder.addProperty("warningTime", border.getWarningTime());
            obj.add("border", jsonBorder);

            obj.addProperty("dimension", world.getDimension().toString());
        }

        return obj;
    }

    public static JsonObject toJson(Vector3i vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getX());
        obj.addProperty("z", vector.getX());
        return obj;
    }
    public static JsonObject toJson(Vector3d vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getY());
        obj.addProperty("z", vector.getZ());
        return obj;
    }
    public static JsonObject toJson(Location<World> location) {
        return toJson(location, true);
    }
    public static JsonObject toJson(Location<World> location, boolean includeWorld) {
        JsonObject obj = new JsonObject();
        if (includeWorld) obj.add("world", toJson(location.getExtent()));
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        return obj;
    }

    public static JsonObject toJson(Entity entity) {
        return toJson(entity, false);
    }
    public static JsonObject toJson(Entity entity, boolean details) {
        JsonObject obj = new JsonObject();
        obj.addProperty("guid", entity.getUniqueId().toString());
        obj.addProperty("type", entity.getType().getId());

        if (details) {
            obj.add("location", toJson(entity.getLocation()));
            obj.add("velocity", toJson(entity.getVelocity()));
            obj.add("rotation", toJson(entity.getRotation()));
            obj.add("data", containerToJson(entity));
            obj.add("properties", propertiesToJson(entity));
            obj.addProperty("creator", Util.getOptUUIDOrNull(entity.getCreator()));
        }

        return obj;
    }

    public static JsonObject toJson(TileEntity te) {
        return toJson(te, false);
    }
    public static JsonObject toJson(TileEntity te, boolean details) {
        JsonObject obj = new JsonObject();
        obj.add("location", toJson(te.getLocation()));
        obj.addProperty("type", te.getType().getId());

        if (details) {
            obj.add("block", toJson(te.getBlock()));
            obj.add("data", containerToJson(te));
            obj.add("properties", propertiesToJson(te));

            if (te instanceof TileEntityCarrier) {
                Inventory inventory = ((TileEntityCarrier) te).getInventory();
                obj.add("inventory", toJson(inventory));
            }
        }

        return obj;
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
                    subArr.add(objectToJsonPrimitive(o));
                }
                obj.add(key, subArr);
            } else if (val instanceof ImmutableList) {
                JsonArray subArr = new JsonArray();
                for (Object o : ((ImmutableList)val)) {
                    subArr.add(objectToJsonPrimitive(o));
                }
                obj.add(key, subArr);
            } else {
                obj.add(key, objectToJsonPrimitive(val));
            }
        }
        return obj;
    }
    private static JsonObject propertiesToJson(PropertyHolder holder) {
        JsonObject obj = new JsonObject();
        Collection<Property<?, ?>> properties = holder.getApplicableProperties();
        for (Property<?, ?> prop : properties) {
            obj.add(Util.lowerFirst(prop.getKey().toString()), objectToJsonPrimitive(prop.getValue()));
        }
        return obj;
    }

    public static JsonArray toJson(Inventory inventory) {
        JsonArray arr = new JsonArray();
        for (Inventory inv : inventory.slots()) {
            Optional<ItemStack> stack = inv.peek();
            if (stack.isPresent()) {
                arr.add(toJson(stack.get()));
            }
        }
        return arr;
    }
    public static JsonObject toJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", stack.getItem().getId());
        obj.addProperty("name", stack.getTranslation().get());
        obj.addProperty("quantity", stack.getQuantity());
        return obj;
    }

    public static JsonObject toJson(BlockState state) {
        JsonObject obj = new JsonObject();
        obj.add("type", toJson(state.getType()));
        obj.add("data", containerToJson(state));

        JsonObject traitObj = new JsonObject();
        Map<BlockTrait<?>, ?> traits = state.getTraitMap();
        for (Map.Entry<BlockTrait<?>, ?> trait : traits.entrySet()) {
            traitObj.add(Util.lowerFirst(trait.getKey().getName()), objectToJsonPrimitive(trait.getValue()));
        }
        obj.add("traits", traitObj);

        return obj;
    }
    public static JsonObject toJson(BlockType type) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", type.getId());
        obj.addProperty("name", type.getTranslation().get());
        obj.add("properties", propertiesToJson(type));
        return obj;
    }

    public static JsonPrimitive objectToJsonPrimitive(Object o) {
        if (o instanceof Number) {
            return new JsonPrimitive((Number)o);
        } else if (o instanceof Boolean) {
            return new JsonPrimitive((Boolean)o);
        }
        return new JsonPrimitive(o.toString());
    }
}
