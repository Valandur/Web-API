package valandur.webapi.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.chat.CachedChatMessage;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;
import valandur.webapi.permission.Permissions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DataCache {

    private static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<CachedCommandCall> commandCalls = new ConcurrentLinkedQueue<>();
    private static Collection<CachedPluginContainer> plugins = new LinkedHashSet<>();
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

    public static Optional<Object> executeMethod(CachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

            Object o = obj.get();
            Method[] ms = Arrays.stream(Util.getAllMethods(o.getClass())).filter(m -> {
                if (!m.getName().equalsIgnoreCase(methodName))
                    return false;

                Class<?>[] reqTypes = m.getParameterTypes();
                if (reqTypes.length != paramTypes.length)
                    return false;

                for (int i = 0; i < reqTypes.length; i++) {
                    if (!reqTypes[i].isAssignableFrom(paramTypes[i])) {
                        return false;
                    }
                }

                return true;
            }).toArray(Method[]::new);

            if (ms.length == 0) {
                return new Exception("Method not found");
            }

            try {
                Method m = ms[0];
                m.setAccessible(true);
                return m.invoke(o, paramValues);
            } catch (Exception e) {
                return e;
            }
        });
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
                    fields.put(fieldName, TextNode.valueOf("ERROR: Field not found"));
                    continue;
                }

                field.get().setAccessible(true);

                try {
                    Object res = field.get().get(obj);
                    fields.put(fieldName, JsonConverter.toJson(res, true, Permissions.permitAllNode()));
                } catch (IllegalAccessException e) {
                    fields.put(fieldName, TextNode.valueOf("ERROR: " + e.toString()));
                }
            }
            for (String methodName : reqMethods) {
                Optional<Method> method = allMethods.stream().filter(f -> f.getName().equalsIgnoreCase(methodName)).findAny();
                if (!method.isPresent()) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method not found"));
                    continue;
                }

                if (method.get().getParameterCount() > 0) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method must not have parameters"));
                    continue;
                }

                if (method.get().getReturnType().equals(Void.TYPE) || method.get().getReturnType().equals(Void.class)) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method must not return void"));
                    continue;
                }

                method.get().setAccessible(true);

                try {
                    Object res = method.get().invoke(obj);
                    methods.put(methodName, JsonConverter.toJson(res, true, Permissions.permitAllNode()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    methods.put(methodName, TextNode.valueOf("ERROR: " + e.toString()));
                }
            }

            return new Tuple<>(fields, methods);
        }).orElse(null);
    }

    public static void updatePlugins() {
        Collection<PluginContainer> plugins = Sponge.getPluginManager().getPlugins();
        Collection<CachedPluginContainer> cachedPlugins = new LinkedHashSet<>();
        for (PluginContainer plugin : plugins) {
            cachedPlugins.add(new CachedPluginContainer(plugin));
        }
        DataCache.plugins = cachedPlugins;
    }
    public static Collection<CachedPluginContainer> getPlugins() {
        return plugins;
    }
    public static Optional<CachedPluginContainer> getPlugin(String id) {
        for (CachedPluginContainer plugin : plugins) {
            if (plugin.getId().equalsIgnoreCase(id))
                return Optional.of(plugin);
        }
        return Optional.empty();
    }
    public static CachedPluginContainer getPlugin(PluginContainer plugin) {
        Optional<CachedPluginContainer> e = getPlugin(plugin.getId());
        return e.orElseGet(() -> new CachedPluginContainer(plugin));
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
            if (cmd.getName().equalsIgnoreCase(name))
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
