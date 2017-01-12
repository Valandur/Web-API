package valandur.webapi.cache;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.Util;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DataCache {

    public static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    public static Collection<CachedPlugin> plugins = new LinkedHashSet<>();
    public static Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    public static Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();
    public static Map<UUID, CachedEntity> entities = new ConcurrentHashMap<>();
    public static Collection<CachedTileEntity> tileEntities = new LinkedHashSet<>();


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
        return tileEntities;
    }
    public static Collection<CachedTileEntity> getTileEntities(CachedWorld world) {
        return tileEntities.stream().filter(te -> te.location.world.uuid.equals(world.uuid)).collect(Collectors.toList());
    }
    public static Optional<CachedTileEntity> getTileEntity(CachedWorld world, double x, double y, double z) {
        for (CachedTileEntity te : tileEntities) {
            if (te.location.position.x == x && te.location.position.y == y && te.location.position.z == z)
                return Optional.of(te);
        }
        return Optional.empty();
    }

    public static Map<String, Object> containerToMap(ValueContainer container) {
        Map<String, Object> map = new HashMap<>();

        // Workaround for missing data on player
        if (container instanceof CompositeValueStore) {
            CompositeValueStore store = (CompositeValueStore)container;
            if (store.supports(FoodData.class)) {
                Optional<ValueContainer> subCont = store.get(FoodData.class);
                if (subCont.isPresent())
                    map.putAll(containerToMap(subCont.get()));
            }
            if (store.supports(DisplayNameData.class)) {
                Optional<ValueContainer> subCont = store.get(DisplayNameData.class);
                if (subCont.isPresent())
                    map.putAll(containerToMap(subCont.get()));
            }

            /*

            WAITING FOR SPONGE TO IMPLEMENT

            if (store.supports(AchievementData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(AchievementData.class).get();
                map.putAll(containerToMap(subCont));
            }
            if (store.supports(DamageableData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(DamageableData.class).get();
                map.putAll(containerToMap(subCont));
            }
            if (store.supports(StatisticData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(StatisticData.class).get();
                map.putAll(containerToMap(subCont));
            }
            if (store.supports(SubjectData.class)) {
                ValueContainer subCont = (ValueContainer)store.get(SubjectData.class).get();
                map.putAll(containerToMap(subCont));
            }

            */
        }

        Set<ImmutableValue<?>> values = container.getValues();
        for (ImmutableValue<?> immutable : values) {
            String key = Util.lowerFirst(immutable.getKey().getQuery().asString("."));
            Object val = immutable.get();

            // Do some formatting for special cases so the data is nicer (especially for json)
            if (val instanceof Text) {
                val = ((Text)val).toPlain();
            } else if (val instanceof Instant) {
                val = ((Instant)val).getEpochSecond();
            }

            map.put(key, val);
        }

        return map;
    }
    public static Map<String, Object> containerToMap(DataContainer container) {
        Map<DataQuery, Object> data = container.getValues(false);
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<DataQuery, Object> entry : data.entrySet()) {
            String key = Util.lowerFirst(entry.getKey().asString("-"));
            Object value = entry.getValue();
            map.put(key, value);
        }
        return map;
    }
    public static Map<String, Object> propertiesToMap(PropertyHolder holder) {
        Map<String, Object> map = new LinkedHashMap<>();
        Collection<Property<?, ?>> properties = holder.getApplicableProperties();
        for (Property<?, ?> prop : properties) {
            String key = Util.lowerFirst(prop.getKey().toString());
            Object val = prop.getValue();
            map.put(key, val);
        }
        return map;
    }
}
