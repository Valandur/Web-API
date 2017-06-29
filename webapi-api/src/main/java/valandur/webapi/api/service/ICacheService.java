package valandur.webapi.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.chat.CachedChatMessage;
import valandur.webapi.api.cache.command.CachedCommand;
import valandur.webapi.api.cache.command.CachedCommandCall;
import valandur.webapi.api.cache.entity.CachedEntity;
import valandur.webapi.api.cache.player.CachedPlayer;
import valandur.webapi.api.cache.plugin.CachedPluginContainer;
import valandur.webapi.api.cache.tileentity.CachedTileEntity;
import valandur.webapi.api.cache.world.CachedWorld;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface ICacheService {

    Long getCacheDurationFor(Class clazz);

    ConcurrentLinkedQueue<CachedChatMessage> getChatMessages();
    ConcurrentLinkedQueue<CachedCommandCall> getCommandCalls();

    Map<Class, JsonNode> getClasses();
    JsonNode getClass(Class type);

    Collection<CachedWorld> getWorlds();
    Optional<CachedWorld> getWorld(String nameOrUuid);
    Optional<CachedWorld> getWorld(UUID uuid);
    CachedWorld getWorld(World world);
    CachedWorld updateWorld(World world);
    CachedWorld updateWorld(WorldProperties world);
    void removeWorld(UUID worldUuid);

    Collection<CachedPlayer> getPlayers();
    Optional<CachedPlayer> getPlayer(UUID uuid);
    CachedPlayer getPlayer(Player player);

    Collection<CachedEntity> getEntities();
    Optional<CachedEntity> getEntity(UUID uuid);
    CachedEntity getEntity(Entity entity);
    CachedEntity updateEntity(Entity entity);
    CachedEntity removeEntity(UUID uuid);

    Collection<CachedPluginContainer> getPlugins();
    Optional<CachedPluginContainer> getPlugin(String id);
    CachedPluginContainer getPlugin(PluginContainer plugin);

    Collection<CachedCommand> getCommands();
    Optional<CachedCommand> getCommand(String name);

    Optional<Collection<CachedTileEntity>> getTileEntities();
    Optional<Collection<CachedTileEntity>> getTileEntities(CachedWorld world);
    Optional<CachedTileEntity> getTileEntity(Location<World> location);
    Optional<CachedTileEntity> getTileEntity(CachedWorld world, int x, int y, int z);

    Tuple<Map<String, JsonNode>, Map<String, JsonNode>> getExtraData(CachedObject cache, String[] reqFields, String[] reqMethods);
    Optional<Object> executeMethod(CachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues);
}
