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
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.Util;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DataCache {
    public static SpongeExecutorService syncExecutor = Sponge.getScheduler().createSyncExecutor(WebAPI.getInstance());

    public static ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    public static Collection<CachedPlugin> plugins = new LinkedHashSet<>();
    public static Collection<CachedWorld> worlds = new LinkedHashSet<>();
    public static Collection<CachedPlayer> players = new LinkedHashSet<>();
    public static Collection<CachedEntity> entities = new LinkedHashSet<>();
    public static Collection<CachedTileEntity> tileEntities = new LinkedHashSet<>();

    public static void update() {
        Collection<World> worlds = Sponge.getServer().getWorlds();
        Collection<CachedWorld> cachedWorlds = new LinkedHashSet<>();
        Collection<CachedEntity> cachedEntities = new LinkedHashSet<>();
        Collection<CachedTileEntity> cachedTileEntities = new LinkedHashSet<>();
        for (World world : worlds) {
            cachedWorlds.add(CachedWorld.copyFrom(world, true));
            Collection<Entity> entities = world.getEntities();
            for (Entity entity : entities) {
                cachedEntities.add(CachedEntity.copyFrom(entity, true));
            }
            Collection<TileEntity> tes = world.getTileEntities();
            for (TileEntity te : tes) {
                cachedTileEntities.add(CachedTileEntity.copyFrom(te, true));
            }
        }
        DataCache.worlds = cachedWorlds;
        DataCache.entities = cachedEntities;
        DataCache.tileEntities = cachedTileEntities;

        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        Collection<CachedPlayer> cachedPlayers = new LinkedHashSet<>();
        for (Player player : players) {
            cachedPlayers.add(CachedPlayer.copyFrom(player, true));
        }
        DataCache.players = cachedPlayers;

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

    public static Collection<CachedWorld> getWorlds() {
        return worlds;
    }
    public static Optional<CachedWorld> getWorld(String worldNameOrUUID) {
        for (CachedWorld world : worlds) {
            if (world.name.equalsIgnoreCase(worldNameOrUUID) || world.uuid.equals(worldNameOrUUID))
                return Optional.of(world);
        }
        return Optional.empty();
    }

    public static Collection<CachedPlayer> getPlayers() {
        return players;
    }
    public static Optional<CachedPlayer> getPlayer(String playerNameOrUUID) {
        for (CachedPlayer player : players) {
            if (player.name.equalsIgnoreCase(playerNameOrUUID) || player.uuid.equals(playerNameOrUUID))
                return Optional.of(player);
        }
        return Optional.empty();
    }

    public static Collection<CachedEntity> getEntities() {
        return entities;
    }
    public static Optional<CachedEntity> getEntity(String entityUUID) {
        for (CachedEntity entity : entities) {
            if (entity.uuid.equals(entityUUID))
                return Optional.of(entity);
        }
        return Optional.empty();
    }

    public static Collection<CachedTileEntity> getTileEntities() {
        return tileEntities;
    }
    public static Collection<CachedTileEntity> getTileEntities(CachedWorld world) {
        return tileEntities.stream().filter(item -> item.location.world.uuid.equals(world.uuid)).collect(Collectors.toList());
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
