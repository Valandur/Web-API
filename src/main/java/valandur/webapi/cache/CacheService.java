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
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
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
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.message.ICachedMessage;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.CachedTransform;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.cache.world.ICachedWorldFull;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.message.CachedChatMessage;
import valandur.webapi.cache.message.CachedMessage;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.player.CachedAdvancement;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedChunk;
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

public class CacheService implements ICacheService {

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

    @Override
    public List<ICachedMessage> getMessages() {
        return Lists.reverse(Arrays.asList(messages.toArray(new ICachedMessage[messages.size()])));
    }
    @Override
    public List<ICachedCommandCall> getCommandCalls() {
        return Lists.reverse(Arrays.asList(commandCalls.toArray(new ICachedCommandCall[commandCalls.size()])));
    }


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

    @Override
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

    @Override
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
    @Override
    public Collection<ICachedWorldFull> getWorlds() {
        return new ArrayList<>(worlds.values());
    }
    @Override
    public Optional<ICachedWorldFull> getWorld(String nameOrUuid) {
        if (Util.isValidUUID(nameOrUuid)) {
            return getWorld(UUID.fromString(nameOrUuid));
        }

        Optional<CachedWorld> world = worlds.values().stream().filter(w -> w.getName().equalsIgnoreCase(nameOrUuid)).findAny();
        return world.flatMap(cachedWorld -> getWorld(cachedWorld.getUUID()));

    }
    @Override
    public Optional<ICachedWorldFull> getWorld(UUID uuid) {
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
    @Override
    public ICachedWorldFull getWorld(World world) {
        Optional<ICachedWorldFull> w = getWorld(world.getUniqueId());
        return w.orElseGet(() -> updateWorld(world));
    }
    @Override
    public ICachedWorldFull updateWorld(World world) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_WORLD.startTiming();
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        Timings.CACHE_WORLD.stopTiming();
        return w;
    }
    @Override
    public ICachedWorldFull updateWorld(WorldProperties world) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_WORLD.startTiming();
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        Timings.CACHE_WORLD.stopTiming();
        return w;
    }
    @Override
    public ICachedWorldFull removeWorld(UUID worldUuid) {
        return worlds.remove(worldUuid);
    }

    @Override
    public Collection<ICachedPlayerFull> getPlayers() {
        return new ArrayList<>(players.values());
    }
    @Override
    public Optional<ICachedPlayerFull> getPlayer(String nameOrUuid) {
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
            return optUser.<ICachedPlayerFull>map(CachedPlayer::new);
        });
    }
    @Override
    public Optional<ICachedPlayerFull> getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return WebAPI.runOnMain(() -> {
                Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
                if (!optSrv.isPresent())
                    throw new InternalServerErrorException("User storage service is not available");

                Optional<User> optUser = optSrv.get().get(uuid);
                return optUser.<ICachedPlayerFull>map(CachedPlayer::new);
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
    @Override
    public ICachedPlayerFull getPlayer(Player player) {
        Optional<ICachedPlayerFull> p = getPlayer(player.getUniqueId());
        return p.orElseGet(() -> updatePlayer(player));
    }
    @Override
    public ICachedPlayerFull getPlayer(User user) {
        Optional<ICachedPlayerFull> p = getPlayer(user.getUniqueId());
        return p.orElseGet(() -> updatePlayer(user));
    }
    @Override
    public ICachedPlayerFull updatePlayer(Player player) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_PLAYER.startTiming();
        CachedPlayer p = new CachedPlayer(player);
        players.put(p.getUUID(), p);
        Timings.CACHE_PLAYER.stopTiming();
        return p;
    }
    @Override
    public ICachedPlayerFull updatePlayer(User user) {
        assert Sponge.getServer().isMainThread();

        Timings.CACHE_PLAYER.startTiming();
        CachedPlayer p = new CachedPlayer(user);
        players.put(p.getUUID(), p);
        Timings.CACHE_PLAYER.stopTiming();
        return p;
    }
    @Override
    public ICachedPlayerFull removePlayer(UUID uuid) {
        return players.remove(uuid);
    }

    @Override
    public Collection<ICachedEntity> getEntities(ICachedWorld world, Vector3i min, Vector3i max,
                                                 Predicate<Entity> predicate, int limit) {
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
            Collection<ICachedEntity> allEnts = new LinkedList<>();
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
    @Override
    public Optional<ICachedEntity> getEntity(UUID uuid) {
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
    @Override
    public Collection<ICachedPluginContainer> getPlugins() {
        return new ArrayList<>(plugins.values());
    }
    @Override
    public Optional<ICachedPluginContainer> getPlugin(String id) {
        if (!plugins.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(plugins.get(id));
    }
    @Override
    public ICachedPluginContainer getPlugin(PluginContainer plugin) {
        Optional<ICachedPluginContainer> e = getPlugin(plugin.getId());
        return e.orElseGet(() -> updatePlugin(plugin));
    }
    @Override
    public ICachedPluginContainer updatePlugin(PluginContainer plugin) {
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
    @Override
    public Collection<ICachedCommand> getCommands() {
        return new ArrayList<>(commands.values());
    }
    @Override
    public Optional<ICachedCommand> getCommand(String name) {
        if (!commands.containsKey(name)) {
            return Optional.empty();
        }

        return Optional.of(commands.get(name));
    }
    @Override
    public ICachedCommand getCommand(CommandMapping command) {
        Optional<ICachedCommand> e = getCommand(command.getPrimaryAlias());
        return e.orElseGet(() -> updateCommand(command));
    }
    @Override
    public ICachedCommand updateCommand(CommandMapping command) {
        CachedCommand c = new CachedCommand(command);
        commands.put(c.getName(), c);
        return c;
    }

    @Override
    public Collection<ICachedTileEntity> getTileEntities(ICachedWorld world, Vector3i min, Vector3i max,
                                                         Predicate<TileEntity> predicate, int limit) {
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
            Collection<ICachedTileEntity> allTes = new LinkedList<>();
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
    @Override
    public Optional<ICachedTileEntity> getTileEntity(Location<World> location) {
        Optional<ICachedWorldFull> w = this.getWorld(location.getExtent().getUniqueId());
        if (!w.isPresent())
            return Optional.empty();

        return getTileEntity(w.get(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    @Override
    public Optional<ICachedTileEntity> getTileEntity(ICachedWorld world, int x, int y, int z) {
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

    @Override
    public Object executeMethod(ICachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
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
