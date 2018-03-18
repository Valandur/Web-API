package valandur.webapi.api.cache;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.message.ICachedMessage;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.cache.world.ICachedWorldFull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * The cache service provides access to all objects which are cached by the Web-API.
 */
public interface ICacheService {

    /**
     * Returns the specified object as a cached object. Performs a deep copy if necessary. Converts all applicable
     * data types to their cached variants. This is especially useful if you're working with an object whose type
     * you don't know. Just call this method on it and the returned object will be a thread safe copy, or null if
     * a thread safe copy cannot be created because the object is of an unknown type.
     * @param obj The object which is returned in it's cached form.
     * @return The cached version of the object. Not necessarily the same type as the original object. {@code Null}
     * if the object is of an unknown type.
     */
    Object asCachedObject(Object obj);

    /**
     * Gets the amount of time a certain object is cached for.
     *
     * @param clazz The class of the object.
     * @return The amount of time in seconds the object is cached for.
     */
    Long getCacheDurationFor(Class clazz);

    /**
     * Gets a history of all the messages sent on the server.
     *
     * @return A list of cached messages.
     */
    List<ICachedMessage> getMessages();

    /**
     * Gets a history of all the commands run on the server.
     *
     * @return The commands run on the server.
     */
    List<ICachedCommandCall> getCommandCalls();

    /**
     * Gets a collection of all the worlds on the server (loaded and unloaded).
     *
     * @return A collection of all the worlds.
     */
    Collection<ICachedWorldFull> getWorlds();

    /**
     * Gets a specific world by name or UUID.
     *
     * @param nameOrUuid Either the name or UUID of the world. Use {@link #getWorld(UUID)} if the UUID is
     *                   already parsed.
     * @return An optional containing the world, or empty if not found.
     */
    Optional<ICachedWorldFull> getWorld(String nameOrUuid);

    /**
     * Gets a specific world by UUID.
     *
     * @param uuid The UUID of the world.
     * @return An optional containing the world, or empty if not found.
     */
    Optional<ICachedWorldFull> getWorld(UUID uuid);

    /**
     * Gets the passed world as a cached object. This method first tries to get the world from the cache, and if
     * it is not found uses the {@link #updateWorld(World)} method to convert it into a cached object.
     *
     * @param world The world which is returned in it's cached form.
     * @return The cached version of the specified world.
     */
    ICachedWorldFull getWorld(World world);

    /**
     * Updates the internal representation of the passed world and returns it.
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    ICachedWorldFull updateWorld(World world);

    /**
     * Updates a world according to the passed world properties (used for unloaded worlds).
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    ICachedWorldFull updateWorld(WorldProperties world);

    /**
     * Removes a world from the cache.
     *
     * @param worldUuid The UUID of the world to remove.
     * @return The removed world representation.
     */
    ICachedWorldFull removeWorld(UUID worldUuid);

    /**
     * Gets all the online players of the server.
     * @return A collection of all the online players.
     */
    Collection<ICachedPlayerFull> getPlayers();

    /**
     * Gets a specific player by name or UUID.
     *
     * @param nameOrUuid Either the name or UUID of the player. Use {@link #getPlayer(UUID)}} if the UUID is
     *                   already parsed.
     * @return An optional containing the player, or empty if not found.
     */
    Optional<ICachedPlayerFull> getPlayer(String nameOrUuid);

    /**
     * Gets a specific player by UUID.
     * @param uuid The UUID of the player.
     * @return An optional containing the cached player if found, or empty otherwise.
     */
    Optional<ICachedPlayerFull> getPlayer(UUID uuid);

    /**
     * Gets the passed player as a cached object. This method first tries to get the player from the cache, and if
     * it is not found uses the {@link #updatePlayer(Player)} method to convert it into a cached object.
     * @param player The player which is returned in it's cached form.
     * @return The cached version of the specified player.
     */
    ICachedPlayerFull getPlayer(Player player);

    /**
     * Gets the passed user as a cached object. This method first tries to get the user from the cache, and if
     * it is not found uses the {@link #updatePlayer(User)} method to convert it into a cached object.
     * @param user The user which is returned in it's cached form.
     * @return The cached version of the specified user.
     */
    ICachedPlayerFull getPlayer(User user);

    /**
     * Updates the internal representation of the passed player and returns it.
     * @param player The player which will be updated.
     * @return The updated cached player.
     */
    ICachedPlayerFull updatePlayer(Player player);

    /**
     * Updates the internal representation of the passed user and returns it.
     * @param user The user which will be updated.
     * @return The updated cached user.
     */
    ICachedPlayerFull updatePlayer(User user);

    /**
     * Removes a player from the cache.
     * @param uuid The UUID of the player to remove.
     * @return The removed player representation.
     */
    ICachedPlayerFull removePlayer(UUID uuid);

    /**
     * Gets a collection of all the entities in the specified world.
     * @param world The world for which all entities are retrieved.
     * @param min The minimum coordinates at which to get entities.
     * @param max The maximum coordinates at which to get entities.
     * @param predicate The predicate to filter entities by.
     * @param limit The maximum amount of entities to return.
     * @return All the entities in the specified world.
     */
    Collection<ICachedEntity> getEntities(ICachedWorld world, Vector3i min, Vector3i max,
                                          Predicate<Entity> predicate, int limit);

    /**
     * Gets a specific entity by UUID.
     * @param uuid The UUID of the entity.
     * @return An optional containing the cached entity if found, or empty otherwise.
     */
    Optional<ICachedEntity> getEntity(UUID uuid);

    /**
     * Gets a collection of all the plugins installed on the server.
     * @return A collection of all the plugins.
     */
    Collection<ICachedPluginContainer> getPlugins();

    /**
     * Gets a specific plugin by id.
     * @param id The id of the plugin.
     * @return An optional containing the plugin if found, or empty otherwise.
     */
    Optional<ICachedPluginContainer> getPlugin(String id);

    /**
     * Gets the passed plugin container as a cached object. This method first tries to get the plugin container from
     * the cache, and if it is not found uses the {@link #updatePlugin(PluginContainer)} method to convert it into a
     * cached object.
     * @param plugin The plugin container which is returned in it's cached form.
     * @return The cached version of the specified plugin container.
     */
    ICachedPluginContainer getPlugin(PluginContainer plugin);

    /**
     * Updates the internal representation of the passed plugin container and returns it.
     * @param plugin The plugin container which will be updated.
     * @return The updated cached plugin container.
     */
    ICachedPluginContainer updatePlugin(PluginContainer plugin);

    /**
     * Gets a collection of all the commands registered on the server.
     * @return A collection of all the commands on the server.
     */
    Collection<ICachedCommand> getCommands();

    /**
     * Gets a specific command by it's primary alias.
     * @param name The name of the command.
     * @return An optional containing the command if found, empty otherwise.
     */
    Optional<ICachedCommand> getCommand(String name);

    /**
     * Gets the passed command as a cached object. This method first tries to get the command from the cache,
     * and if it is not found uses the {@link #updateCommand(CommandMapping)} method to convert it into a
     * cached object.
     * @param command The command which is returned in it's cached form.
     * @return The cached version of the specified command.
     */
    public ICachedCommand getCommand(CommandMapping command);

    /**
     * Updates the internal representation of the passed command and returns it.
     * @param command The command which will be updated.
     * @return The updated cached command.
     */
    ICachedCommand updateCommand(CommandMapping command);

    /**
     * Gets a collection of all the tile entities in the specified world.
     * @param world The world for which all tile entities are retrieved.
     * @param min The minimum coordinates at which to get tile entities.
     * @param max The maximum coordinates at which to get tile entities.
     * @param predicate The predicate to filter tile entities by.
     * @param limit The maximum amount of tile entities to return.
     * @return A list of all the tile entities in the specified world.
     */
    Collection<ICachedTileEntity> getTileEntities(ICachedWorld world, Vector3i min, Vector3i max,
                                                  Predicate<TileEntity> predicate, int limit);

    /**
     * Tries to get a tile entity at the specified location.
     * @param location The location of the tile entity.
     * @return An optional containing the tile entity if it was found, empty otherwise.
     */
    Optional<ICachedTileEntity> getTileEntity(Location<World> location);

    /**
     * Tries to get a tile entity at the specified location.
     * @param world The world in which the tile entity is located.
     * @param x The x-coordinate of the location where the tile entity is located.
     * @param y The y-coordinate of the location where the tile entity is located.
     * @param z The z-coordinate of the location where the tile entity is located.
     * @return An optional containing the tile entity if it was found, empty otherwise.
     */
    Optional<ICachedTileEntity> getTileEntity(ICachedWorld world, int x, int y, int z);

    /**
     * Executes the specified method on the provided cached object.
     * @param cache The cached object on which the method is executed.
     * @param methodName The method to execute.
     * @param paramTypes An array containing the types of the parameters of the method (used to distinguish similar
     *                   methods)
     * @param paramValues The parameter values that are passed to the method.
     * @return The result of the method, or empty otherwise. Methods that return void will return
     * {@link Boolean} {@code true} here.
     */
    Object executeMethod(ICachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues);
}
