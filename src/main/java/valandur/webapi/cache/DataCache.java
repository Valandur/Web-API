package valandur.webapi.cache;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;

import java.util.*;
import java.util.concurrent.*;

public class DataCache {

    public static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    public static Collection<CachedPlugin> plugins = new LinkedHashSet<>();
    public static Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    public static Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();
    public static Map<UUID, CachedEntity> entities = new ConcurrentHashMap<>();


    public static void addWorld(World world) {
        worlds.put(world.getUniqueId(), CachedWorld.copyFrom(world));
    }
    public static void removeWorld(UUID uuid) {
        worlds.remove(uuid);
    }
    public static Optional<CachedWorld> updateWorld(UUID uuid) {
        CompletableFuture<Optional<CachedWorld>> future = CompletableFuture.supplyAsync(() -> {
            Optional<World> world = Sponge.getServer().getWorld(uuid);
            if (!world.isPresent()) {
                return Optional.empty();
            }

            CachedWorld w = CachedWorld.copyFrom(world.get(), true);
            worlds.put(world.get().getUniqueId(), w);
            return Optional.of(w);
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
    public static Optional<CachedPlayer> updatePlayer(UUID uuid) {
        CompletableFuture<Optional<CachedPlayer>> future = CompletableFuture.supplyAsync(() -> {
            Optional<Player> player = Sponge.getServer().getPlayer(uuid);
            if (!player.isPresent()) {
                return Optional.empty();
            }

            CachedPlayer p = CachedPlayer.copyFrom(player.get(), true);
            players.put(player.get().getUniqueId(), p);
            return Optional.of(p);
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
    public static Optional<CachedEntity> updateEntity(UUID uuid) {
        CompletableFuture<Optional<CachedEntity>> future = CompletableFuture.supplyAsync(() -> {
            Optional<Entity> entity = Optional.empty();
            for (World world : Sponge.getServer().getWorlds()) {
                Optional<Entity> ent = world.getEntity(uuid);
                if (ent.isPresent()) {
                    entity = ent;
                    break;
                }
            }
            if (!entity.isPresent()) {
                return Optional.empty();
            }

            CachedEntity e = CachedEntity.copyFrom(entity.get(), true);
            entities.put(entity.get().getUniqueId(), e);
            return Optional.of(e);
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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

    public static Collection<CachedTileEntity> getTileEntities() {
        CompletableFuture<Collection<CachedTileEntity>> future = CompletableFuture.supplyAsync(() -> {
            Collection<CachedTileEntity> entities = new ArrayList<>();

            for (World world : Sponge.getServer().getWorlds()) {
                Collection<TileEntity> ents = world.getTileEntities();
                for (TileEntity te : ents) {
                    entities.add(CachedTileEntity.copyFrom(te));
                }
            }

            return entities;
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static Collection<CachedTileEntity> getTileEntities(CachedWorld world) {
        CompletableFuture<Collection<CachedTileEntity>> future = CompletableFuture.supplyAsync(() -> {
            Collection<CachedTileEntity> entities = new ArrayList<>();
            World w = Sponge.getServer().getWorld(UUID.fromString(world.uuid)).get();

            Collection<TileEntity> ents = w.getTileEntities();
            for (TileEntity te : ents) {
                if (!te.isValid()) continue;
                entities.add(CachedTileEntity.copyFrom(te));
            }

            return entities;
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static Optional<CachedTileEntity> getTileEntity(CachedWorld world, int x, int y, int z) {
        CompletableFuture<Optional<CachedTileEntity>> future = CompletableFuture.supplyAsync(() -> {
            World w = Sponge.getServer().getWorld(UUID.fromString(world.uuid)).get();

            Optional<TileEntity> ent = w.getTileEntity(x, y, z);
            if (!ent.isPresent() || !ent.get().isValid()) {
                return Optional.empty();
            }

            return Optional.of(CachedTileEntity.copyFrom(ent.get(), true));
        }, WebAPI.getInstance().syncExecutor);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
