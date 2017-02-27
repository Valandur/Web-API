package valandur.webapi.cache;

import com.google.gson.JsonElement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.JsonConverter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class DataCache {

    private static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<CachedCommandCall> commandCalls = new ConcurrentLinkedQueue<>();
    private static Collection<CachedPlugin> plugins = new LinkedHashSet<>();
    private static Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    private static Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();
    private static Map<UUID, CachedEntity> entities = new ConcurrentHashMap<>();
    private static Map<Class, JsonElement> classes = new ConcurrentHashMap<>();

    public static ConcurrentLinkedQueue<CachedChatMessage> getChatMessages() {
        return chatMessages;
    }
    public static ConcurrentLinkedQueue<CachedCommandCall> getCommandCalls() { return commandCalls; }
    public static Map<Class, JsonElement> getClasses() {
        return classes;
    }


    public static JsonElement getClass(Class type) {
        if (classes.containsKey(type))
            return classes.get(type);

        JsonElement e = JsonConverter.classToJson(type);
        classes.put(type, e);
        return e;
    }

    public static void addChatMessage(Player sender, Text text) {
        chatMessages.add(CachedChatMessage.copyFrom(new Date(), sender, text));

        while (chatMessages.size() > CacheConfig.chatMessages) {
            chatMessages.poll();
        }
    }

    public static void addCommandCall(String command, String arguments, JsonElement source, CommandResult result) {
        commandCalls.add(CachedCommandCall.copyFrom(command, arguments, source, result));

        while (commandCalls.size() > CacheConfig.commandCalls) {
            commandCalls.poll();
        }
    }

    public static JsonElement getRawLive(CachedObject object) {
        Optional<JsonElement> json = runOnMainThread(() -> {
            Optional<Object> o = object.getLive();
            return JsonConverter.toRawJson(o);
        });
        if (!json.isPresent())
            return JsonConverter.toRawJson(Optional.empty());
        return json.get();
    }
    public static Optional<JsonElement> executeMethod(CachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
        return runOnMainThread(() -> {
            Optional<Object> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

            Object o = obj.get();

            try {
                Method m = o.getClass().getMethod(methodName, paramTypes);
                Object res = m.invoke(o, paramValues);
                return JsonConverter.toRawJson(res);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
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
    public static Optional<CachedWorld> getWorld(UUID uuid) {
        if (!worlds.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedWorld res = worlds.get(uuid);
        if (res.isExpired() || !res.hasDetails()) {
            return updateWorld(uuid);
        } else {
            return Optional.of(res);
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
    public static Optional<CachedPlayer> getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedPlayer res = players.get(uuid);
        if (res.isExpired() || !res.hasDetails()) {
            return updatePlayer(uuid);
        } else {
            return Optional.of(res);
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
    public static Optional<CachedEntity> getEntity(UUID uuid) {
        if (!entities.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedEntity res = entities.get(uuid);
        if (res.isExpired() || !res.hasDetails()) {
            return updateEntity(uuid);
        } else {
            return Optional.of(res);
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

    public static Optional<Collection<CachedTileEntity>> getTileEntities() {
        return runOnMainThread(() -> {
            Collection<CachedTileEntity> entities = new ArrayList<>();

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

            Collection<CachedTileEntity> entities = new ArrayList<>();
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
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, WebAPI.getInstance().syncExecutor);
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
