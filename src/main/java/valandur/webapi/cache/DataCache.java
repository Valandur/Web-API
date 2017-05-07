package valandur.webapi.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

public class DataCache {

    private static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<CachedCommandCall> commandCalls = new ConcurrentLinkedQueue<>();
    private static Collection<CachedPlugin> plugins = new LinkedHashSet<>();
    private static Collection<CachedCommand> commands = new LinkedHashSet<>();
    private static Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    private static Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();
    private static Map<UUID, CachedEntity> entities = new ConcurrentHashMap<>();
    private static Map<Class, JsonNode> classes = new ConcurrentHashMap<>();

    public static ConcurrentLinkedQueue<CachedChatMessage> getChatMessages() {
        return chatMessages;
    }
    public static ConcurrentLinkedQueue<CachedCommandCall> getCommandCalls() { return commandCalls; }
    public static Map<Class, JsonNode> getClasses() {
        return classes;
    }


    public static JsonNode getClass(Class type) {
        if (classes.containsKey(type))
            return classes.get(type);

        JsonNode e = JsonConverter.classToJson(type);
        classes.put(type, e);
        return e;
    }

    public static CachedChatMessage addChatMessage(Player sender, MessageEvent event) {
        CachedChatMessage cache = new CachedChatMessage(sender, event);
        chatMessages.add(cache);

        while (chatMessages.size() > CacheConfig.numChatMessages) {
            chatMessages.poll();
        }

        return cache;
    }

    public static CachedCommandCall addCommandCall(SendCommandEvent event) {
        CachedCommandCall cache = new CachedCommandCall(event);
        commandCalls.add(cache);

        while (commandCalls.size() > CacheConfig.numCommandCalls) {
            commandCalls.poll();
        }

        return cache;
    }

    public static JsonNode executeMethod(CachedObject cache, String methodName, Object[] paramValues) {
        Optional<JsonNode> node = WebAPI.runOnMain(() -> {
            Optional<?> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

            Object o = obj.get();
            Method[] ms = Util.getAllMethods(o.getClass());
            Optional<Method> optMethod = Arrays.stream(ms).filter(m -> m.getName().equalsIgnoreCase(methodName)).findAny();

            if (!optMethod.isPresent()) {
                return JsonConverter.toJson("Method not found");
            }

            try {
                Method m = optMethod.get();
                if (m.getParameterCount() != paramValues.length) {
                    return JsonConverter.toJson("Method must have " + m.getParameterCount() +
                            " parameters but has " + paramValues.length);
                }
                m.setAccessible(true);
                Object res = m.invoke(o, paramValues);
                return JsonConverter.toJson(res, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        return node.orElseGet(JsonNodeFactory.instance::nullNode);
    }

    public static CachedWorld getWorld(World world) {
        Optional<CachedWorld> w = getWorld(world.getUniqueId());
        return w.orElseGet(() -> addWorld(world));
    }
    public static CachedWorld addWorld(World world) {
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        return w;
    }
    public static CachedWorld removeWorld(UUID uuid) {
        return worlds.remove(uuid);
    }
    private static Optional<CachedWorld> updateWorld(UUID uuid) {
        return WebAPI.runOnMain(() -> {
            Optional<World> world = Sponge.getServer().getWorld(uuid);
            return world.map(DataCache::addWorld).orElse(null);
        });
    }
    public static Collection<CachedWorld> getWorlds() {
        return worlds.values();
    }
    public static Optional<CachedWorld> getWorld(UUID uuid) {
        if (!worlds.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedWorld res = worlds.get(uuid);
        if (res.isExpired()) {
            return updateWorld(uuid);
        } else {
            return Optional.of(res);
        }
    }

    public static CachedPlayer getPlayer(Player player) {
        Optional<CachedPlayer> p = getPlayer(player.getUniqueId());
        return p.orElseGet(() -> addPlayer(player));
    }
    public static CachedPlayer addPlayer(Player player) {
        CachedPlayer p = new CachedPlayer(player);
        players.put(player.getUniqueId(), p);
        return p;
    }
    public static CachedPlayer removePlayer(UUID uuid) {
        return players.remove(uuid);
    }
    private static Optional<CachedPlayer> updatePlayer(UUID uuid) {
        return WebAPI.runOnMain(() -> {
            Optional<Player> player = Sponge.getServer().getPlayer(uuid);
            return player.map(DataCache::addPlayer).orElse(null);

        });
    }
    public static Collection<CachedPlayer> getPlayers() {
        return players.values();
    }
    public static Optional<CachedPlayer> getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedPlayer res = players.get(uuid);
        if (res.isExpired()) {
            return updatePlayer(uuid);
        } else {
            return Optional.of(res);
        }
    }

    public static CachedEntity getEntity(Entity entity) {
        Optional<CachedEntity> e = getEntity(entity.getUniqueId());
        return e.orElseGet(() -> addEntity(entity));
    }
    public static CachedEntity addEntity(Entity entity) {
        CachedEntity e = new CachedEntity(entity);
        entities.put(entity.getUniqueId(), e);
        return e;
    }
    public static CachedEntity removeEntity(UUID uuid) {
        return entities.remove(uuid);
    }
    private static Optional<CachedEntity> updateEntity(UUID uuid) {
        return WebAPI.runOnMain(() -> {
            Optional<Entity> entity = Optional.empty();
            for (World world : Sponge.getServer().getWorlds()) {
                Optional<Entity> ent = world.getEntity(uuid);
                if (ent.isPresent()) {
                    entity = ent;
                    break;
                }
            }
            return entity.map(DataCache::addEntity).orElse(null);
        });
    }
    public static Collection<CachedEntity> getEntities() {
        return entities.values();
    }
    public static Optional<CachedEntity> getEntity(UUID uuid) {
        if (!entities.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedEntity res = entities.get(uuid);
        if (res.isExpired()) {
            return updateEntity(uuid);
        } else {
            return Optional.of(res);
        }
    }

    public static Tuple<Map<String, JsonNode>, Map<String, JsonNode>> getExtraData(CachedObject cache, String[] reqFields, String[] reqMethods) {
        return WebAPI.runOnMain(() -> {
            Map<String, JsonNode> fields = new HashMap<>();
            Map<String, JsonNode> methods = new HashMap<>();

            Optional<?> opt = cache.getLive();
            if (!opt.isPresent()) return null;

            Object obj = opt.get();
            Class c = obj.getClass();

            List<Field> allFields = Arrays.asList(Util.getAllFields(c));
            List<Method> allMethods = Arrays.asList(Util.getAllMethods(c));
            for (String fieldName : reqFields) {
                Optional<Field> field = allFields.stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findAny();
                if (!field.isPresent()) {
                    fields.put(fieldName, JsonConverter.toJson("ERROR: Field not found"));
                    continue;
                }

                field.get().setAccessible(true);

                try {
                    Object res = field.get().get(obj);
                    fields.put(fieldName, JsonConverter.toJson(res));
                } catch (IllegalAccessException e) {
                    fields.put(fieldName, JsonConverter.toJson("ERROR: " + e.toString()));
                }
            }
            for (String methodName : reqMethods) {
                Optional<Method> method = allMethods.stream().filter(f -> f.getName().equalsIgnoreCase(methodName)).findAny();
                if (!method.isPresent()) {
                    methods.put(methodName, JsonConverter.toJson("ERROR: Method not found"));
                    continue;
                }

                if (method.get().getParameterCount() > 0) {
                    methods.put(methodName, JsonConverter.toJson("ERROR: Method must not have parameters"));
                    continue;
                }

                if (method.get().getReturnType().equals(Void.TYPE) || method.get().getReturnType().equals(Void.class)) {
                    methods.put(methodName, JsonConverter.toJson("ERROR: Method must not return void"));
                    continue;
                }

                method.get().setAccessible(true);

                try {
                    Object res = method.get().invoke(obj);
                    methods.put(methodName, JsonConverter.toJson(res));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    methods.put(methodName, JsonConverter.toJson("ERROR: " + e.toString()));
                }
            }

            return new Tuple<>(fields, methods);
        }).orElse(null);
    }

    public static void updatePlugins() {
        Collection<PluginContainer> plugins = Sponge.getPluginManager().getPlugins();
        Collection<CachedPlugin> cachedPlugins = new LinkedHashSet<>();
        for (PluginContainer plugin : plugins) {
            cachedPlugins.add(new CachedPlugin(plugin));
        }
        DataCache.plugins = cachedPlugins;
    }
    public static Collection<CachedPlugin> getPlugins() {
        return plugins;
    }
    public static Optional<CachedPlugin> getPlugin(String id) {
        for (CachedPlugin plugin : plugins) {
            if (plugin.id.equalsIgnoreCase(id))
                return Optional.of(plugin);
        }
        return Optional.empty();
    }

    public static void updateCommands() {
        Collection<CommandMapping> commands = Sponge.getCommandManager().getAll().values();
        Collection<CachedCommand> cachedCommands = new LinkedHashSet<>();
        for (CommandMapping cmd : commands) {
            cachedCommands.add(new CachedCommand(cmd));
        }
        DataCache.commands = cachedCommands;
    }
    public static Collection<CachedCommand> getCommands() {
        return commands;
    }
    public static Optional<CachedCommand> getCommand(String name) {
        for (CachedCommand cmd : commands) {
            if (cmd.name.equalsIgnoreCase(name))
                return Optional.of(cmd);
        }
        return Optional.empty();
    }

    public static Optional<Collection<CachedTileEntity>> getTileEntities() {
        return WebAPI.runOnMain(() -> {
            Collection<CachedTileEntity> entities = new LinkedList<>();

            for (World world : Sponge.getServer().getWorlds()) {
                Collection<TileEntity> ents = world.getTileEntities();
                for (TileEntity te : ents) {
                    entities.add(new CachedTileEntity(te));
                }
            }

            return entities;
        });
    }
    public static Optional<Collection<CachedTileEntity>> getTileEntities(CachedWorld world) {
        return WebAPI.runOnMain(() -> {
            Optional<?> w = world.getLive();
            if (!w.isPresent())
                return null;

            Collection<CachedTileEntity> entities = new LinkedList<>();
            Collection<TileEntity> ents = ((World)w.get()).getTileEntities();
            for (TileEntity te : ents) {
                if (!te.isValid()) continue;
                entities.add(new CachedTileEntity(te));
            }

            return entities;
        });
    }
    public static Optional<CachedTileEntity> getTileEntity(Location<World> location) {
        Optional<CachedWorld> w = DataCache.getWorld(location.getExtent().getUniqueId());
        if (!w.isPresent())
            return Optional.empty();

        return getTileEntity(w.get(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    public static Optional<CachedTileEntity> getTileEntity(CachedWorld world, int x, int y, int z) {
        return WebAPI.runOnMain(() -> {
            Optional<?> w = world.getLive();
            if (!w.isPresent())
                return null;

            Optional<TileEntity> ent = ((World)w.get()).getTileEntity(x, y, z);
            if (!ent.isPresent() || !ent.get().isValid()) {
                return null;
            }

            return new CachedTileEntity(ent.get());
        });
    }
}
