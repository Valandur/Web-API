package valandur.webapi.cache;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.json.JsonConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

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

    public static CachedChatMessage addChatMessage(Player sender, Text text) {
        CachedChatMessage cache = CachedChatMessage.copyFrom(sender, text);
        chatMessages.add(cache);

        while (chatMessages.size() > CacheConfig.numChatMessages) {
            chatMessages.poll();
        }

        return cache;
    }

    public static CachedCommandCall addCommandCall(String command, String arguments, JsonNode source, CommandResult result) {
        CachedCommandCall cache = CachedCommandCall.copyFrom(command, arguments, source, result);
        commandCalls.add(cache);

        while (commandCalls.size() > CacheConfig.numCommandCalls) {
            commandCalls.poll();
        }

        return cache;
    }

    public static JsonNode executeMethod(CachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
        Optional<JsonNode> node = runOnMainThread(() -> {
            Optional<Object> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

            Object o = obj.get();
            Method[] ms = JsonConverter.getAllMethods(o.getClass());
            Optional<Method> optMethod = Arrays.stream(ms).filter(m -> m.getName().equalsIgnoreCase(methodName)).findAny();

            if (!optMethod.isPresent()) {
                return null;
            }

            try {
                Method m = optMethod.get(); o.getClass().getMethod(methodName, paramTypes);
                m.setAccessible(true);
                Object res = m.invoke(o, paramValues);
                return JsonConverter.toJson(res, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        if (!node.isPresent())
            return JsonConverter.toJson(null);
        return node.get();
    }
    public static JsonNode getField(CachedObject cache, String fieldName) {
        Optional<JsonNode> node = runOnMainThread(() -> {
            Optional<Object> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

            Object o = obj.get();
            Field[] fs = JsonConverter.getAllFields(o.getClass());
            Optional<Field> optField = Arrays.stream(fs).filter(f -> f.getName().equalsIgnoreCase(fieldName)).findAny();

            if (!optField.isPresent()) {
                return null;
            }

            try {
                Field field = optField.get();
                field.setAccessible(true);
                Object res = field.get(o);
                return JsonConverter.toJson(res, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        if (!node.isPresent())
            return JsonConverter.toJson(null);
        return node.get();
    }

    public static void addWorld(World world) {
        worlds.put(world.getUniqueId(), CachedWorld.copyFrom(world));
    }
    public static void removeWorld(UUID uuid) {
        worlds.remove(uuid);
    }
    private static Optional<CachedWorld> updateWorld(UUID uuid) {
        return runOnMainThread(() -> {
            Optional<World> world = Sponge.getServer().getWorld(uuid);
            if (!world.isPresent())
                return null;

            CachedWorld w = CachedWorld.copyFrom(world.get(), true);
            worlds.put(world.get().getUniqueId(), w);
            return w;
        });
    }
    public static Collection<CachedWorld> getWorlds() {
        return worlds.values();
    }
    public static Optional<CachedWorld> getWorld(UUID uuid, boolean details) {
        if (!worlds.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedWorld res = worlds.get(uuid);
        if (!details || (res.hasDetails() && !res.isExpired())) {
            return Optional.of(res);
        } else {
            return updateWorld(uuid);
        }
    }

    public static void addPlayer(Player player) {
        players.put(player.getUniqueId(), CachedPlayer.copyFrom(player));
    }
    public static void removePlayer(UUID uuid) {
        players.remove(uuid);
    }
    private static Optional<CachedPlayer> updatePlayer(UUID uuid) {
        return runOnMainThread(() -> {
            Optional<Player> player = Sponge.getServer().getPlayer(uuid);
            if (!player.isPresent())
                return null;

            CachedPlayer p = CachedPlayer.copyFrom(player.get(), true);
            players.put(player.get().getUniqueId(), p);
            return p;
        });
    }
    public static Collection<CachedPlayer> getPlayers() {
        return players.values();
    }
    public static Optional<CachedPlayer> getPlayer(UUID uuid, boolean details) {
        if (!players.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedPlayer res = players.get(uuid);
        if (!details || (res.hasDetails() && !res.isExpired())) {
            return Optional.of(res);
        } else {
            return updatePlayer(uuid);
        }
    }

    public static void addEntity(Entity entity) {
        entities.put(entity.getUniqueId(), CachedEntity.copyFrom(entity));
    }
    public static void removeEntity(UUID uuid) {
        entities.remove(uuid);
    }
    private static Optional<CachedEntity> updateEntity(UUID uuid) {
        return runOnMainThread(() -> {
            Optional<Entity> entity = Optional.empty();
            for (World world : Sponge.getServer().getWorlds()) {
                Optional<Entity> ent = world.getEntity(uuid);
                if (ent.isPresent()) {
                    entity = ent;
                    break;
                }
            }
            if (!entity.isPresent()) {
                return null;
            }

            CachedEntity e = CachedEntity.copyFrom(entity.get(), true);
            entities.put(entity.get().getUniqueId(), e);
            return e;
        });
    }
    public static Collection<CachedEntity> getEntities() {
        return entities.values();
    }
    public static Optional<CachedEntity> getEntity(UUID uuid, boolean details) {
        if (!entities.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedEntity res = entities.get(uuid);
        if (!details || (res.hasDetails() && !res.isExpired())) {
            return Optional.of(res);
        } else {
            return updateEntity(uuid);
        }
    }

    public static void updatePlugins() {
        Collection<PluginContainer> plugins = Sponge.getPluginManager().getPlugins();
        Collection<CachedPlugin> cachedPlugins = new LinkedHashSet<>();
        for (PluginContainer plugin : plugins) {
            cachedPlugins.add(CachedPlugin.copyFrom(plugin));
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
            cachedCommands.add(CachedCommand.copyFrom(cmd));
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
        return runOnMainThread(() -> {
            Collection<CachedTileEntity> entities = new LinkedList<>();

            for (World world : Sponge.getServer().getWorlds()) {
                Collection<TileEntity> ents = world.getTileEntities();
                for (TileEntity te : ents) {
                    entities.add(CachedTileEntity.copyFrom(te));
                }
            }

            return entities;
        });
    }
    public static Optional<Collection<CachedTileEntity>> getTileEntities(CachedWorld world) {
        return runOnMainThread(() -> {
            Optional<Object> w = world.getLive();
            if (!w.isPresent())
                return null;

            Collection<CachedTileEntity> entities = new LinkedList<>();
            Collection<TileEntity> ents = ((World)w.get()).getTileEntities();
            for (TileEntity te : ents) {
                if (!te.isValid()) continue;
                entities.add(CachedTileEntity.copyFrom(te));
            }

            return entities;
        });
    }
    public static Optional<CachedTileEntity> getTileEntity(CachedWorld world, int x, int y, int z) {
        return runOnMainThread(() -> {
            Optional<Object> w = world.getLive();
            if (!w.isPresent())
                return null;

            Optional<TileEntity> ent = ((World)w.get()).getTileEntity(x, y, z);
            if (!ent.isPresent() || !ent.get().isValid()) {
                return null;
            }

            return CachedTileEntity.copyFrom(ent.get(), true);
        });
    }

    private static <T> Optional<T> runOnMainThread(Supplier<T> supplier) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, WebAPI.syncExecutor);
        try {
            T obj = future.get();
            if (obj == null)
                return Optional.empty();
            return Optional.of(obj);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
