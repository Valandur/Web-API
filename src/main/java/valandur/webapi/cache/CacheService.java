package valandur.webapi.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.message.CachedChatMessage;
import valandur.webapi.cache.message.CachedMessage;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.player.CachedAdvancement;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedChunk;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.cache.world.CachedTransform;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.config.CacheConfig;
import valandur.webapi.util.Timings;
import valandur.webapi.util.Util;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

/**
 * The cache service provides access to all objects which are cached by the Web-API.
 */
public class CacheService {

    private static final String configFileName = "cache.conf";

    private List<String> censoredCommands = new ArrayList<>();
    private Map<String, Long> cacheDurations = new HashMap<>();
    private int numChatMessages;
    private int numCommandCalls;

    private ConcurrentLinkedQueue<CachedMessage> messages = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<CachedCommandCall> commandCalls = new ConcurrentLinkedQueue<>();
    private Map<String, CachedCommand> commands = new ConcurrentHashMap<>();
    private Map<String, CachedPluginContainer> plugins = new ConcurrentHashMap<>();
    private Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    private Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();


    public void init() {
        CacheConfig config = Util.loadConfig(configFileName, new CacheConfig());

        numChatMessages = config.chat_amount;
        numCommandCalls = config.cmd_amount;

        censoredCommands.clear();
        for (String cmd : config.censoredCommands) {
            if (!cmd.startsWith("/"))
                cmd = "/" + cmd;
            censoredCommands.add(cmd);
        }

        cacheDurations.clear();
        for (Map.Entry<String, Long> entry : config.duration.entrySet()) {
            cacheDurations.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets a history of all the messages sent on the server.
     *
     * @return A list of cached messages.
     */
    public List<CachedMessage> getMessages() {
        return Lists.reverse(Arrays.asList(messages.toArray(new CachedMessage[0])));
    }


    /**
     * Gets a history of all the commands run on the server.
     *
     * @return The commands run on the server.
     */
    public List<CachedCommandCall> getCommandCalls() {
        return Lists.reverse(Arrays.asList(commandCalls.toArray(new CachedCommandCall[0])));
    }


    /**
     * Returns the specified object as a cached object. Performs a deep copy if necessary. Converts all applicable
     * data types to their cached variants. This is especially useful if you're working with an object whose type
     * you don't know. Just call this method on it and the returned object will be a thread safe copy, or null if
     * a thread safe copy cannot be created because the object is of an unknown type.
     * @param obj The object which is returned in it's cached form.
     * @return The cached version of the object. Not necessarily the same type as the original object. {@code Null}
     * if the object is of an unknown type.
     */
    public Object asCachedObject(Object obj) {
        // If the object is already clearly a cached object then just return it
        if (obj instanceof CachedObject)
            return obj;

        // If the object is a primitive type we don't have to do anything either
        if (obj instanceof Boolean)
            return obj;
        if (obj instanceof Integer)
            return obj;
        if (obj instanceof Float)
            return obj;
        if (obj instanceof Double)
            return obj;
        if (obj instanceof String)
            return obj;

        // Other POJOs
        if (obj instanceof Instant)
            return Instant.ofEpochMilli(((Instant)obj).toEpochMilli());

        // If the object is of a type that we have a cached version for, then create one of those
        if (obj instanceof Player)
            return getPlayer((Player)obj);
        if (obj instanceof World)
            return getWorld((World)obj);
        if (obj instanceof Entity)
            return new CachedEntity((Entity)obj);
        if (obj instanceof Chunk)
            return new CachedChunk((Chunk)obj);
        if (obj instanceof TileEntity)
            return new CachedTileEntity((TileEntity)obj);
        if (obj instanceof PluginContainer)
            return getPlugin((PluginContainer)obj);
        if (obj instanceof Inventory)
            return new CachedInventory((Inventory)obj);
        if (obj instanceof CommandMapping)
            return getCommand((CommandMapping)obj);
        if (obj instanceof Advancement)
            return new CachedAdvancement((Advancement)obj);

        if (obj instanceof ItemStack)
            return ((ItemStack)obj).copy();
        if (obj instanceof ItemStackSnapshot)
            return ((ItemStackSnapshot)obj).copy();

        if (obj instanceof Cause)
            return new CachedCause((Cause)obj);
        if (obj instanceof Location)
            return new CachedLocation((Location)obj);
        if (obj instanceof Transform)
            return new CachedTransform((Transform)obj);
        if (obj instanceof CatalogType)
            return new CachedCatalogType((CatalogType)obj);

        // If this is an unknown object type then we can't create a cached version of it, so we better not try
        // and save it because we don't know if it's thread safe or not.
        return obj.getClass().getName();
    }


    /**
     * Gets the amount of time a certain object is cached for.
     *
     * @param clazz The class of the object.
     * @return The amount of time in seconds the object is cached for.
     */
    public Long getCacheDurationFor(Class clazz) {
        Long dur = cacheDurations.get(clazz.getSimpleName());
        return dur != null ? dur : 0;
    }


    public void updateWorlds() {
        WebAPI.runOnMain(() -> {
            worlds.clear();

            // The worlds that are loaded on server start are overwritten by the world load event later
            // TODO: Sponge has a bug that makes the "getAllWorldProperties" method cache the world properties
            Collection<WorldProperties> unloadedWorlds = Sponge.getServer().getAllWorldProperties();
            for (WorldProperties world : unloadedWorlds) {
                updateWorld(world);
            }
        });
    }

    /**
     * Gets a collection of all the worlds on the server (loaded and unloaded).
     *
     * @return A collection of all the worlds.
     */
    public Collection<CachedWorld> getWorlds() {
        return new ArrayList<>(worlds.values());
    }

    /**
     * Gets a specific world by name or UUID.
     *
     * @param nameOrUuid Either the name or UUID of the world. Use {@link #getWorld(UUID)} if the UUID is
     *                   already parsed.
     * @return An optional containing the world, or empty if not found.
     */
    public Optional<CachedWorld> getWorld(String nameOrUuid) {
        if (Util.isValidUUID(nameOrUuid)) {
            return getWorld(UUID.fromString(nameOrUuid));
        }

        Optional<CachedWorld> world = worlds.values().stream().filter(w -> w.getName().equalsIgnoreCase(nameOrUuid)).findAny();
        return world.flatMap(cachedWorld -> getWorld(cachedWorld.getUUID()));

    }

    /**
     * Gets a specific world by UUID.
     *
     * @param uuid The UUID of the world.
     * @return An optional containing the world, or empty if not found.
     */
    public Optional<CachedWorld> getWorld(UUID uuid) {
        if (!worlds.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedWorld res = worlds.get(uuid);
        if (res.isExpired()) {
            return Optional.of(WebAPI.runOnMain(() -> {
                Optional<World> world = Sponge.getServer().getWorld(uuid);
                if (world.isPresent())
                    return updateWorld(world.get());
                Optional<WorldProperties> props = Sponge.getServer().getWorldProperties(uuid);
                if (!props.isPresent())
                    throw new InternalServerErrorException("Could not get world properties");
                return updateWorld(props.get());
            }));
        } else {
            return Optional.of(res);
        }
    }

    /**
     * Gets the passed world as a cached object. This method first tries to get the world from the cache, and if
     * it is not found uses the {@link #updateWorld(World)} method to convert it into a cached object.
     *
     * @param world The world which is returned in it's cached form.
     * @return The cached version of the specified world.
     */
    public CachedWorld getWorld(World world) {
        Optional<CachedWorld> w = getWorld(world.getUniqueId());
        return w.orElseGet(() -> updateWorld(world));
    }

    /**
     * Updates the internal representation of the passed world and returns it.
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    public CachedWorld updateWorld(World world) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_WORLD.startTiming();
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        Timings.CACHE_WORLD.stopTiming();
        return w;
    }

    /**
     * Updates a world according to the passed world properties (used for unloaded worlds).
     *
     * @param world The world which will be updated.
     * @return The updated cached world.
     */
    public CachedWorld updateWorld(WorldProperties world) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_WORLD.startTiming();
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        Timings.CACHE_WORLD.stopTiming();
        return w;
    }

    /**
     * Removes a world from the cache.
     *
     * @param worldUuid The UUID of the world to remove.
     * @return The removed world representation.
     */
    public CachedWorld removeWorld(UUID worldUuid) {
        return worlds.remove(worldUuid);
    }


    /**
     * Gets all the online players of the server.
     * @return A collection of all the online players.
     */
    public Collection<CachedPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * Gets a specific player by name or UUID.
     *
     * @param nameOrUuid Either the name or UUID of the player. Use {@link #getPlayer(UUID)}} if the UUID is
     *                   already parsed.
     * @return An optional containing the player, or empty if not found.
     */
    public Optional<CachedPlayer> getPlayer(String nameOrUuid) {
        if (Util.isValidUUID(nameOrUuid)) {
            return getPlayer(UUID.fromString(nameOrUuid));
        }

        Optional<CachedPlayer> player = players.values().stream().filter(p -> p.getName().equalsIgnoreCase(nameOrUuid)).findAny();
        if (player.isPresent())
            return player.flatMap(p -> getPlayer(p.getUUID()));

        return WebAPI.runOnMain(() -> {
            Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
            if (!optSrv.isPresent())
                throw new InternalServerErrorException("User storage service is not available");

            Optional<User> optUser = optSrv.get().get(nameOrUuid);
            return optUser.<CachedPlayer>map(CachedPlayer::new);
        });
    }

    /**
     * Gets a specific player by UUID.
     * @param uuid The UUID of the player.
     * @return An optional containing the cached player if found, or empty otherwise.
     */
    public Optional<CachedPlayer> getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return WebAPI.runOnMain(() -> {
                Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
                if (!optSrv.isPresent())
                    throw new InternalServerErrorException("User storage service is not available");

                Optional<User> optUser = optSrv.get().get(uuid);
                return optUser.<CachedPlayer>map(CachedPlayer::new);
            });
        }

        final CachedPlayer res = players.get(uuid);
        if (res.isExpired()) {
            return WebAPI.runOnMain(() -> {
                Optional<Player> player = Sponge.getServer().getPlayer(uuid);
                return player.map(this::updatePlayer);
            });
        } else {
            return Optional.of(res);
        }
    }

    /**
     * Gets the passed player as a cached object. This method first tries to get the player from the cache, and if
     * it is not found uses the {@link #updatePlayer(Player)} method to convert it into a cached object.
     * @param player The player which is returned in it's cached form.
     * @return The cached version of the specified player.
     */
    public CachedPlayer getPlayer(Player player) {
        Optional<CachedPlayer> p = getPlayer(player.getUniqueId());
        return p.orElseGet(() -> updatePlayer(player));
    }

    /**
     * Gets the passed user as a cached object. This method first tries to get the user from the cache, and if
     * it is not found uses the {@link #updatePlayer(User)} method to convert it into a cached object.
     * @param user The user which is returned in it's cached form.
     * @return The cached version of the specified user.
     */
    public CachedPlayer getPlayer(User user) {
        Optional<CachedPlayer> p = getPlayer(user.getUniqueId());
        return p.orElseGet(() -> updatePlayer(user));
    }

    /**
     * Updates the internal representation of the passed player and returns it.
     * @param player The player which will be updated.
     * @return The updated cached player.
     */
    public CachedPlayer updatePlayer(Player player) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_PLAYER.startTiming();
        CachedPlayer p = new CachedPlayer(player);
        players.put(p.getUUID(), p);
        Timings.CACHE_PLAYER.stopTiming();
        return p;
    }

    /**
     * Updates the internal representation of the passed user and returns it.
     * @param user The user which will be updated.
     * @return The updated cached user.
     */
    public CachedPlayer updatePlayer(User user) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_PLAYER.startTiming();
        CachedPlayer p = new CachedPlayer(user);
        players.put(p.getUUID(), p);
        Timings.CACHE_PLAYER.stopTiming();
        return p;
    }

    /**
     * Removes a player from the cache.
     * @param uuid The UUID of the player to remove.
     * @return The removed player representation.
     */
    public CachedPlayer removePlayer(UUID uuid) {
        return players.remove(uuid);
    }


    /**
     * Gets a collection of all the entities in the specified world.
     * @param world The world for which all entities are retrieved.
     * @param min The minimum coordinates at which to get entities.
     * @param max The maximum coordinates at which to get entities.
     * @param predicate The predicate to filter entities by.
     * @param limit The maximum amount of entities to return.
     * @return All the entities in the specified world.
     */
    public Collection<CachedEntity> getEntities(
            CachedWorld world,
            Vector3i min,
            Vector3i max,
            Predicate<Entity> predicate,
            int limit) {
        return WebAPI.runOnMain(() -> {
            Stream<Extent> extents;
            if (world == null)
                extents = Sponge.getServer().getWorlds().stream().map(w -> (Extent)w);
            else {
                Optional<?> w = world.getLive();
                if (!w.isPresent())
                    throw new InternalServerErrorException("Could not get live world");
                extents = Stream.of((Extent)w.get());
            }

            if (min != null) {
                extents = extents.map(e -> e.getExtentView(min, e.getBlockMax()));
            }
            if (max != null) {
                extents = extents.map(e -> e.getExtentView(e.getBlockMin(), max));
            }

            int i = 0;
            Collection<CachedEntity> allEnts = new LinkedList<>();
            Iterator<Extent> iter = extents.iterator();
            while (iter.hasNext()) {
                Extent ext = iter.next();
                Collection<Entity> ents = ext.getEntities(predicate);
                for (Entity e : ents) {
                    if (e.isRemoved()) continue;
                    allEnts.add(new CachedEntity(e));

                    i++;
                    if (limit > 0 && i >= limit)
                        break;
                }
            }

            return allEnts;
        });
    }

    /**
     * Gets a specific entity by UUID.
     * @param uuid The UUID of the entity.
     * @return An optional containing the cached entity if found, or empty otherwise.
     */
    public Optional<CachedEntity> getEntity(UUID uuid) {
        return WebAPI.runOnMain(() -> {
            Collection<World> worlds = Sponge.getServer().getWorlds();
            for (World world : worlds) {
                Optional<Entity> optEnt = world.getEntity(uuid);
                if (optEnt.isPresent()) {
                    return Optional.of(new CachedEntity(optEnt.get()));
                }
            }
            return Optional.empty();
        });
    }


    public void updatePlugins() {
        assert Sponge.getServer().isMainThread();

        plugins.clear();

        Collection<PluginContainer> newPlugins = Sponge.getPluginManager().getPlugins();
        for (PluginContainer plugin : newPlugins) {
            plugins.put(plugin.getId(), new CachedPluginContainer(plugin));
        }

        // Look for .jar files in the current directory, and compare their "mcmod.info" file
        // with the data we have of the loaded plugins to see if there are any unloaded ones.
        try {
            List<Path> paths = Files.walk(Paths.get("./"))
                    .map(Path::normalize)
                    .filter(p -> Files.isRegularFile(p) && !p.toString().startsWith("libraries"))
                    .filter(p -> p.toString().endsWith(".jar") || p.toString().endsWith(".disabled"))
                    .collect(Collectors.toList());

            for (Path path : paths) {
                JarFile jarFile = new JarFile(path.toFile());
                ZipEntry entry = jarFile.getEntry("mcmod.info");
                if (entry == null) {
                    continue;
                }

                Optional<PluginContainer> exists = newPlugins.stream()
                        .filter(p -> p.getSource().isPresent() && p.getSource().get().equals(path.normalize()))
                        .findAny();
                if (exists.isPresent()) {
                    continue;
                }

                InputStream inputStream = jarFile.getInputStream(entry);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(result.toString(StandardCharsets.UTF_8.name()));
                root.forEach(n -> {
                    if (plugins.containsKey(n.path("modid").asText())) {
                        if (path.toString().endsWith(".jar.disabled")) {
                            plugins.get(n.path("modid").asText()).setWillBeUnloaded();
                        }
                        return;
                    }
                    plugins.put(n.path("modid").asText(), new CachedPluginContainer(n, path));
                });
            }
        } catch (IOException ignored) {}
    }

    /**
     * Gets a collection of all the plugins installed on the server.
     * @return A collection of all the plugins.
     */
    public Collection<CachedPluginContainer> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    /**
     * Gets a specific plugin by id.
     * @param id The id of the plugin.
     * @return An optional containing the plugin if found, or empty otherwise.
     */
    public Optional<CachedPluginContainer> getPlugin(String id) {
        if (!plugins.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(plugins.get(id));
    }

    /**
     * Gets the passed plugin container as a cached object. This method first tries to get the plugin container from
     * the cache, and if it is not found uses the {@link #updatePlugin(PluginContainer)} method to convert it into a
     * cached object.
     * @param plugin The plugin container which is returned in it's cached form.
     * @return The cached version of the specified plugin container.
     */
    public CachedPluginContainer getPlugin(PluginContainer plugin) {
        Optional<CachedPluginContainer> e = getPlugin(plugin.getId());
        return e.orElseGet(() -> updatePlugin(plugin));
    }

    /**
     * Updates the internal representation of the passed plugin container and returns it.
     * @param plugin The plugin container which will be updated.
     * @return The updated cached plugin container.
     */
    public CachedPluginContainer updatePlugin(PluginContainer plugin) {
        CachedPluginContainer c = new CachedPluginContainer(plugin);
        plugins.put(c.getId(), c);
        return c;
    }


    public void updateCommands() {
        assert Sponge.getServer().isMainThread();

        commands.clear();

        Collection<CommandMapping> newCommands = Sponge.getCommandManager().getAll().values();
        for (CommandMapping cmd : newCommands) {
            if (commands.containsKey(cmd.getPrimaryAlias()))
                continue;
            commands.put(cmd.getPrimaryAlias(), new CachedCommand(cmd));
        }
    }

    /**
     * Gets a collection of all the commands registered on the server.
     * @return A collection of all the commands on the server.
     */
    public Collection<CachedCommand> getCommands() {
        return new ArrayList<>(commands.values());
    }

    /**
     * Gets a specific command by it's primary alias.
     * @param name The name of the command.
     * @return An optional containing the command if found, empty otherwise.
     */
    public Optional<CachedCommand> getCommand(String name) {
        if (!commands.containsKey(name)) {
            return Optional.empty();
        }

        return Optional.of(commands.get(name));
    }

    /**
     * Gets the passed command as a cached object. This method first tries to get the command from the cache,
     * and if it is not found uses the {@link #updateCommand(CommandMapping)} method to convert it into a
     * cached object.
     * @param command The command which is returned in it's cached form.
     * @return The cached version of the specified command.
     */
    public CachedCommand getCommand(CommandMapping command) {
        Optional<CachedCommand> e = getCommand(command.getPrimaryAlias());
        return e.orElseGet(() -> updateCommand(command));
    }

    /**
     * Updates the internal representation of the passed command and returns it.
     * @param command The command which will be updated.
     * @return The updated cached command.
     */
    public CachedCommand updateCommand(CommandMapping command) {
        CachedCommand c = new CachedCommand(command);
        commands.put(c.getName(), c);
        return c;
    }


    /**
     * Gets a collection of all the tile entities in the specified world.
     * @param world The world for which all tile entities are retrieved.
     * @param min The minimum coordinates at which to get tile entities.
     * @param max The maximum coordinates at which to get tile entities.
     * @param predicate The predicate to filter tile entities by.
     * @param limit The maximum amount of tile entities to return.
     * @return A list of all the tile entities in the specified world.
     */
    public Collection<CachedTileEntity> getTileEntities(
            CachedWorld world,
            Vector3i min,
            Vector3i max,
            Predicate<TileEntity> predicate,
            int limit) {
        return WebAPI.runOnMain(() -> {
            Stream<Extent> extents;
            if (world == null)
                extents = Sponge.getServer().getWorlds().stream().map(w -> (Extent)w);
            else {
                Optional<?> w = world.getLive();
                if (!w.isPresent())
                    throw new InternalServerErrorException("Could not get live world");
                extents = Stream.of((Extent)w.get());
            }

            if (min != null) {
                extents = extents.map(e -> e.getExtentView(min, e.getBlockMax()));
            }
            if (max != null) {
                extents = extents.map(e -> e.getExtentView(e.getBlockMin(), max));
            }

            int i = 0;
            Collection<CachedTileEntity> allTes = new LinkedList<>();
            Iterator<Extent> iter = extents.iterator();
            while (iter.hasNext()) {
                Extent ext = iter.next();
                Collection<TileEntity> tes = ext.getTileEntities(predicate);
                for (TileEntity te : tes) {
                    if (!te.isValid()) continue;
                    allTes.add(new CachedTileEntity(te));

                    i++;
                    if (limit > 0 && i >= limit)
                        break;
                }
            }

            return allTes;
        });
    }

    /**
     * Tries to get a tile entity at the specified location.
     * @param location The location of the tile entity.
     * @return An optional containing the tile entity if it was found, empty otherwise.
     */
    public Optional<CachedTileEntity> getTileEntity(Location<World> location) {
        Optional<CachedWorld> w = this.getWorld(location.getExtent().getUniqueId());
        if (!w.isPresent())
            return Optional.empty();

        return getTileEntity(w.get(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Tries to get a tile entity at the specified location.
     * @param world The world in which the tile entity is located.
     * @param x The x-coordinate of the location where the tile entity is located.
     * @param y The y-coordinate of the location where the tile entity is located.
     * @param z The z-coordinate of the location where the tile entity is located.
     * @return An optional containing the tile entity if it was found, empty otherwise.
     */
    public Optional<CachedTileEntity> getTileEntity(CachedWorld world, int x, int y, int z) {
        return WebAPI.runOnMain(() -> {
            Optional<?> w = world.getLive();
            if (!w.isPresent())
                throw new InternalServerErrorException("Could not get live world");

            Optional<TileEntity> ent = ((World)w.get()).getTileEntity(x, y, z);
            if (!ent.isPresent() || !ent.get().isValid()) {
                return Optional.empty();
            }

            return Optional.of(new CachedTileEntity(ent.get()));
        });
    }

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
    public Object executeMethod(CachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = cache.getLive();

            if (!obj.isPresent())
                throw new InternalServerErrorException("Could not get live version of object");

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
                throw new NotFoundException("Could not find requested method");
            }

            try {
                Method m = ms[0];
                m.setAccessible(true);
                Object res = m.invoke(o, paramValues);
                if (m.getReturnType() == Void.class || m.getReturnType() == void.class)
                    return true;
                return res;
            } catch (Exception e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        });
    }

    // Server events
    @Listener(order = Order.POST)
    public void onWorldLoad(LoadWorldEvent event) {
        updateWorld(event.getTargetWorld());
    }
    @Listener(order = Order.POST)
    public void onWorldUnload(UnloadWorldEvent event) {
        updateWorld(event.getTargetWorld().getProperties());
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        updatePlayer(event.getTargetEntity());
    }
    @Listener(order = Order.POST)
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        removePlayer(event.getTargetEntity().getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onUserKick(KickPlayerEvent event) {
        removePlayer(event.getTargetEntity().getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onMessage(MessageChannelEvent event) {
        Optional<Player> player = event.getCause().first(Player.class);

        MessageChannel channel = event.getChannel().orElse(event.getOriginalChannel());
        CachedMessage msg = player.isPresent() ?
                new CachedChatMessage(player.get(), channel.getMembers(), event.getMessage()) :
                new CachedMessage(channel.getMembers(), event.getMessage());
        messages.add(msg);

        while (messages.size() > numChatMessages) {
            messages.poll();
        }
    }
    @Listener(order = Order.POST)
    public void onCommand(SendCommandEvent event) {
        CachedCommandCall cache = new CachedCommandCall(event, censoredCommands.contains(event.getCommand()));
        commandCalls.add(cache);

        while (commandCalls.size() > numCommandCalls) {
            commandCalls.poll();
        }
    }
}
