package valandur.webapi.api.cache;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.chat.ICachedChatMessage;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.*;

public interface ICacheService {

    Long getCacheDurationFor(Class clazz);

    List<ICachedChatMessage> getChatMessages();
    List<ICachedCommandCall> getCommandCalls();

    Map<Class, JsonNode> getClasses();
    JsonNode getClass(Class type);

    Collection<ICachedWorld> getWorlds();
    Optional<ICachedWorld> getWorld(String nameOrUuid);
    Optional<ICachedWorld> getWorld(UUID uuid);
    ICachedWorld getWorld(World world);
    ICachedWorld updateWorld(World world);
    ICachedWorld updateWorld(WorldProperties world);
    void removeWorld(UUID worldUuid);

    Collection<ICachedPlayer> getPlayers();
    Optional<ICachedPlayer> getPlayer(UUID uuid);
    ICachedPlayer getPlayer(Player player);

    Collection<ICachedEntity> getEntities();
    Optional<ICachedEntity> getEntity(UUID uuid);
    ICachedEntity getEntity(Entity entity);
    ICachedEntity updateEntity(Entity entity);
    ICachedEntity removeEntity(UUID uuid);

    Collection<ICachedPluginContainer> getPlugins();
    Optional<ICachedPluginContainer> getPlugin(String id);
    ICachedPluginContainer getPlugin(PluginContainer plugin);

    Collection<ICachedCommand> getCommands();
    Optional<ICachedCommand> getCommand(String name);

    Optional<Collection<ICachedTileEntity>> getTileEntities();
    Optional<Collection<ICachedTileEntity>> getTileEntities(ICachedWorld world);
    Optional<ICachedTileEntity> getTileEntity(Location<World> location);
    Optional<ICachedTileEntity> getTileEntity(ICachedWorld world, int x, int y, int z);

    Tuple<Map<String, JsonNode>, Map<String, JsonNode>> getExtraData(ICachedObject cache, String[] reqFields, String[] reqMethods);
    Optional<Object> executeMethod(ICachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues);
}
