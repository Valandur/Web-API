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

/**
 * The cache service provides access to all objects which are cached by the Web-API.
 */
public interface ICacheService {

    /**
     * Gets the amount of time a certain object is cached for.
     *
     * @param clazz The class of the object.
     * @return The amount of time in seconds the object is cached for.
     */
    Long getCacheDurationFor(Class clazz);

    /**
     * Gets a history of all the chat messages sent on the server.
     *
     * @return A list of cached chat messages.
     */
    List<ICachedChatMessage> getChatMessages();

    /**
     * Gets a history of all the commands run on the server.
     *
     * @return The commands run on the server.
     */
    List<ICachedCommandCall> getCommandCalls();

    /**
     * Gets all the cached classes.
     *
     * @return A map of cached classes to their json representation.
     */
    Map<Class, JsonNode> getClasses();

    /**
     * Gets the json representation of a specific class. If it is cached it will be returned from the cache,
     * otherwise the json is computed and then saved in the cache for future use.
     *
     * @param type The class to get the json representation for.
     * @return The json which represents the class.
     */
    JsonNode getClass(Class type);

    /**
     * Gets a collection of all the worlds on the server (loaded and unloaded).
     *
     * @return A collection of all the worlds.
     */
    Collection<ICachedWorld> getWorlds();

    /**
     * Gets a specific world by name or UUID.
     *
     * @param nameOrUuid Either the name or UUID of the world. Use {@link #getWorld(UUID)} if the UUID is
     *                   already parsed.
     * @return An optional containing the world, or empty if not found.
     */
    Optional<ICachedWorld> getWorld(String nameOrUuid);

    /**
     * Gets a specific world by UUID.
     *
     * @param uuid The UUID of the world.
     * @return An optional containing the world, or empty if not found.
     */
    Optional<ICachedWorld> getWorld(UUID uuid);

    /**
     * Gets the passed world as a cached object. This method first tries to get the world from the cache, and if
     * it is not found uses the {@link #updateWorld(World)} method to convert it into a cached object.
     *
     * @param world The world which is returned in it's cached form.
     * @return The cached version of the specified world.
     */
    ICachedWorld getWorld(World world);

    /**
     * Updates the internal representation of the passed world and returns it.
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    ICachedWorld updateWorld(World world);

    /**
     * Updates a world according to the passed world properties (used for unloaded worlds).
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    ICachedWorld updateWorld(WorldProperties world);

    /**
     * Removes a world from the cache.
     *
     * @param worldUuid The UUID of the world to remove.
     * @return The removed world representation.
     */
    ICachedWorld removeWorld(UUID worldUuid);

    /**
     * Gets all the online players of the server.
     * @return A collection of all the online players.
     */
    Collection<ICachedPlayer> getPlayers();

    /**
     * Gets a specific player by UUID.
     * @param uuid The UUID of the player.
     * @return An optional containing the cached player if found, or empty otherwise.
     */
    Optional<ICachedPlayer> getPlayer(UUID uuid);

    /**
     * Gets the passed player as a cached object. This method first tries to get the player from the cache, and if
     * it is not found uses the {@link #updatePlayer(Player)} method to convert it into a cached object.
     * @param player The player which is returned in it's cached form.
     * @return The cached version of the specified player.
     */
    ICachedPlayer getPlayer(Player player);

    /**
     * Updates the internal representation of the passed player and returns it.
     * @param player The player which will be updated.
     * @return The updated cached player.
     */
    ICachedPlayer updatePlayer(Player player);

    /**
     * Removes a player from the cache.
     * @param uuid The UUID of the player to remove.
     * @return The removed player representation.
     */
    ICachedPlayer removePlayer(UUID uuid);

    /**
     * Gets a collection of all the entities on the server.
     * @return A collection of all the entities.
     */
    Collection<ICachedEntity> getEntities();

    /**
     * Gets a specific entity by UUID.
     * @param uuid The UUID of the entity.
     * @return An optional containing the cached entity if found, or empty otherwise.
     */
    Optional<ICachedEntity> getEntity(UUID uuid);

    /**
     * Gets the passed entity as a cached object. This method first tries to get the entity from the cache, and if
     * it is not found uses the {@link #updateEntity(Entity)} method to convert it into a cached object.
     * @param entity The entity which is returned in it's cached form.
     * @return The cached version of the specified entity.
     */
    ICachedEntity getEntity(Entity entity);

    /**
     * Updates the internal representation of the passed entity and returns it.
     * @param entity The entity which will be updated.
     * @return The updated cached entity.
     */
    ICachedEntity updateEntity(Entity entity);

    /**
     * Removes an entity from the cache.
     * @param uuid The UUID of the entity to remove.
     * @return The removed entity representation.
     */
    ICachedEntity removeEntity(UUID uuid);

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
     * Gets the passed plugin as a cached object. This method first tries to get the plugin from the cache, and if
     * it is not found converts it into a cached object.
     * @param plugin The plugin which is returned in it's cached form.
     * @return The cached version of the specified plugin.
     */
    ICachedPluginContainer getPlugin(PluginContainer plugin);

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
     * Tries to get a collection of all the tile entities on the server.
     * @return An optional of all the tile entities on the server if the operation was successful, empty otherwise.
     */
    Optional<Collection<ICachedTileEntity>> getTileEntities();

    /**
     * Tries to get a collection of all the tile entities in the specified world.
     * @param world The world for which all tile entities are retrieved.
     * @return An optional containing the tile entities if the operation was successful, empty otherwise.
     */
    Optional<Collection<ICachedTileEntity>> getTileEntities(ICachedWorld world);

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
     * This method provides a convenient access to get arbitrary properties from a cached object in one call. The
     * {@code reqFields} parameter contains the names of all the fields to get, and {@code reqMethods} contains all
     * the names of methods with no parameters and a return value to get.
     * @param cache The cached object for which the data is requested.
     * @param reqFields An array containing all the names of fields to get for the cached object.
     * @param reqMethods An array containing all the names of the methods to get for the cached object.
     *                   The methods must not have any parameters, and must return a value.
     * @return A tuple containing the map of field names to values as the first argument, and a map of method names
     * to values as the second argument
     */
    Tuple<Map<String, JsonNode>, Map<String, JsonNode>> getExtraData(ICachedObject cache, String[] reqFields, String[] reqMethods);

    /**
     * Executes the specified method on the provided cached object.
     * @param cache The cached object on which the method is executed.
     * @param methodName The method to execute.
     * @param paramTypes An array containing the types of the parameters of the method (used to distinguish similar
     *                   methods)
     * @param paramValues The parameter values that are passed to the method.
     * @return An optional containing the result of the method, or empty otherwise. Methods that return void will
     * return {@link Boolean} {@code true} here.
     */
    Optional<Object> executeMethod(ICachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues);
}
