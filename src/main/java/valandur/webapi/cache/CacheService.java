package valandur.webapi.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.chat.ICachedChatMessage;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.cache.chat.CachedChatMessage;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedChunk;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.SerializeService;
import valandur.webapi.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class CacheService implements ICacheService {

    private static final String configFileName = "cache.conf";

    private SerializeService json;

    private Map<String, Long> cacheDurations = new HashMap<>();
    private int numChatMessages;
    private int numCommandCalls;

    private ConcurrentLinkedQueue<CachedChatMessage> chatMessages = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<CachedCommandCall> commandCalls = new ConcurrentLinkedQueue<>();
    private Map<String, CachedCommand> commands = new ConcurrentHashMap<>();
    private Map<String, CachedPluginContainer> plugins = new ConcurrentHashMap<>();
    private Map<UUID, CachedWorld> worlds = new ConcurrentHashMap<>();
    private Map<UUID, CachedPlayer> players = new ConcurrentHashMap<>();
    private Map<UUID, CachedEntity> entities = new ConcurrentHashMap<>();
    private Map<Class, JsonNode> classes = new ConcurrentHashMap<>();

    public List<ICachedChatMessage> getChatMessages() {
        return Arrays.asList(chatMessages.toArray(new ICachedChatMessage[0]));
    }
    public List<ICachedCommandCall> getCommandCalls() {
        return Arrays.asList(commandCalls.toArray(new ICachedCommandCall[0]));
    }


    public void init() {
        this.json = WebAPI.getSerializeService();

        Tuple<ConfigurationLoader, ConfigurationNode> tup = Util.loadWithDefaults(configFileName, "defaults/" + configFileName);
        ConfigurationNode config = tup.getSecond();

        ConfigurationNode amountNode = config.getNode("amount");
        numChatMessages = amountNode.getNode("chat").getInt();
        numCommandCalls = amountNode.getNode("command").getInt();

        cacheDurations.clear();
        ConfigurationNode durationNode = config.getNode("duration");
        for (ConfigurationNode node : durationNode.getChildrenMap().values()) {
            cacheDurations.put(node.getKey().toString(), node.getLong());
        }
    }

    @Override
    public Object asCachedObject(Object obj) {
        Class c = obj.getClass();

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

        // If the object is of a type that we have a cached version for, then create one of those
        if (obj instanceof Player)
            return getPlayer((Player)obj);
        if (obj instanceof Entity)
            return getEntity((Entity)obj);
        if (obj instanceof World)
            return getWorld((World)obj);
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

        if (obj instanceof ItemStack)
            return ((ItemStack)obj).copy();
        if (obj instanceof ItemStackSnapshot)
            return ((ItemStackSnapshot)obj).copy();

        if (obj instanceof Cause)
            return new CachedCause((Cause)obj);
        if (obj instanceof Location)
            return new CachedLocation((Location)obj);
        if (obj instanceof CatalogType)
            return new CachedCatalogType((CatalogType)obj);



        // If this is an unkown object type then we can't create a cached version of it, so we better not try
        // and save it because we don't know if it's thread safe or not.
        return obj.getClass().getName();
    }

    @Override
    public Long getCacheDurationFor(Class clazz) {
        Long dur = cacheDurations.get(clazz.getSimpleName());
        return dur != null ? dur : Long.MAX_VALUE;
    }

    public Map<Class, JsonNode> getClasses() {
        return classes;
    }
    @Override
    public JsonNode getClass(Class type) {
        if (classes.containsKey(type))
            return classes.get(type);

        JsonNode e = json.classToJson(type);
        classes.put(type, e);
        return e;
    }

    public CachedChatMessage addChatMessage(Player sender, MessageEvent event) {
        CachedChatMessage cache = new CachedChatMessage(sender, event);
        chatMessages.add(cache);

        while (chatMessages.size() > numChatMessages) {
            chatMessages.poll();
        }

        return cache;
    }
    public CachedCommandCall addCommandCall(SendCommandEvent event) {
        CachedCommandCall cache = new CachedCommandCall(event);
        commandCalls.add(cache);

        while (commandCalls.size() > numCommandCalls) {
            commandCalls.poll();
        }

        return cache;
    }

    public void updateWorlds() {
        WebAPI.runOnMain(() -> {
            worlds.clear();

            // The worlds that are loaded on server start are overwritten by the world load event later
            Collection<WorldProperties> unloadedWorlds = Sponge.getServer().getAllWorldProperties();
            for (WorldProperties world : unloadedWorlds) {
                updateWorld(world);
            }
        });
    }
    @Override
    public Collection<ICachedWorld> getWorlds() {
        return new ArrayList<>(worlds.values());
    }
    @Override
    public Optional<ICachedWorld> getWorld(String nameOrUuid) {
        if (Util.isValidUUID(nameOrUuid)) {
            return getWorld(UUID.fromString(nameOrUuid));
        }

        Optional<CachedWorld> world = worlds.values().stream().filter(w -> w.getName().equalsIgnoreCase(nameOrUuid)).findAny();
        return world.flatMap(cachedWorld -> getWorld(cachedWorld.getUUID()));

    }
    @Override
    public Optional<ICachedWorld> getWorld(UUID uuid) {
        if (!worlds.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedWorld res = worlds.get(uuid);
        if (res.isExpired()) {
            return WebAPI.runOnMain(() -> {
                Optional<World> world = Sponge.getServer().getWorld(uuid);
                if (world.isPresent())
                    return updateWorld(world.get());
                Optional<WorldProperties> props = Sponge.getServer().getWorldProperties(uuid);
                return props.map(this::updateWorld).orElse(null);
            });
        } else {
            return Optional.of(res);
        }
    }
    @Override
    public ICachedWorld getWorld(World world) {
        Optional<ICachedWorld> w = getWorld(world.getUniqueId());
        return w.orElseGet(() -> updateWorld(world));
    }
    @Override
    public ICachedWorld updateWorld(World world) {
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        return w;
    }
    @Override
    public ICachedWorld updateWorld(WorldProperties world) {
        CachedWorld w = new CachedWorld(world);
        worlds.put(world.getUniqueId(), w);
        return w;
    }
    @Override
    public ICachedWorld removeWorld(UUID worldUuid) {
        return worlds.remove(worldUuid);
    }

    @Override
    public Collection<ICachedPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }
    @Override
    public Optional<ICachedPlayer> getPlayer(String nameOrUuid) {
        if (Util.isValidUUID(nameOrUuid)) {
            return getPlayer(UUID.fromString(nameOrUuid));
        }

        Optional<CachedPlayer> player = players.values().stream().filter(p -> p.getName().equalsIgnoreCase(nameOrUuid)).findAny();
        if (player.isPresent())
            return player.flatMap(p -> getPlayer(p.getUUID()));

        return WebAPI.runOnMain(() -> {
            Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
            if (!optSrv.isPresent())
                return null;

            Optional<User> optUser = optSrv.get().get(nameOrUuid);
            return optUser.<ICachedPlayer>map(CachedPlayer::new).orElse(null);
        });
    }
    @Override
    public Optional<ICachedPlayer> getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            return WebAPI.runOnMain(() -> {
                Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
                if (!optSrv.isPresent())
                    return null;

                Optional<User> optUser = optSrv.get().get(uuid);
                return optUser.<ICachedPlayer>map(CachedPlayer::new).orElse(null);

            });
        }

        final CachedPlayer res = players.get(uuid);
        if (res.isExpired()) {
            return WebAPI.runOnMain(() -> {
                Optional<Player> player = Sponge.getServer().getPlayer(uuid);
                return player.map(this::updatePlayer).orElse(null);

            });
        } else {
            return Optional.of(res);
        }
    }
    @Override
    public ICachedPlayer getPlayer(Player player) {
        Optional<ICachedPlayer> p = getPlayer(player.getUniqueId());
        return p.orElseGet(() -> updatePlayer(player));
    }
    @Override
    public ICachedPlayer getPlayer(User user) {
        Optional<ICachedPlayer> p = getPlayer(user.getUniqueId());
        return p.orElseGet(() -> updatePlayer(user));
    }
    @Override
    public ICachedPlayer updatePlayer(Player player) {
        CachedPlayer p = new CachedPlayer(player);
        players.put(p.getUUID(), p);
        return p;
    }
    @Override
    public ICachedPlayer updatePlayer(User user) {
        CachedPlayer p = new CachedPlayer(user);
        players.put(p.getUUID(), p);
        return p;
    }
    @Override
    public ICachedPlayer removePlayer(UUID uuid) {
        return players.remove(uuid);
    }

    @Override
    public Collection<ICachedEntity> getEntities() {
        return new ArrayList<>(entities.values());
    }
    @Override
    public Optional<ICachedEntity> getEntity(UUID uuid) {
        if (!entities.containsKey(uuid)) {
            return Optional.empty();
        }

        final CachedEntity res = entities.get(uuid);
        if (res.isExpired()) {
            return WebAPI.runOnMain(() -> {
                Optional<Entity> entity = Optional.empty();
                for (World world : Sponge.getServer().getWorlds()) {
                    Optional<Entity> ent = world.getEntity(uuid);
                    if (ent.isPresent()) {
                        entity = ent;
                        break;
                    }
                }
                return entity.map(this::updateEntity).orElse(null);
            });
        } else {
            return Optional.of(res);
        }
    }
    @Override
    public ICachedEntity getEntity(Entity entity) {
        Optional<ICachedEntity> e = getEntity(entity.getUniqueId());
        return e.orElseGet(() -> updateEntity(entity));
    }
    @Override
    public ICachedEntity updateEntity(Entity entity) {
        CachedEntity e = new CachedEntity(entity);
        entities.put(e.getUUID(), e);
        return e;
    }
    @Override
    public ICachedEntity removeEntity(UUID uuid) {
        return entities.remove(uuid);
    }

    public void updatePlugins() {
        plugins.clear();

        Collection<PluginContainer> newPlugins = Sponge.getPluginManager().getPlugins();
        for (PluginContainer plugin : newPlugins) {
            plugins.put(plugin.getId(), new CachedPluginContainer(plugin));
        }
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
    public Optional<Collection<ICachedTileEntity>> getTileEntities(Predicate<TileEntity> predicate, int limit) {
        return WebAPI.runOnMain(() -> {
            Collection<ICachedTileEntity> entities = new LinkedList<>();

            int i = 0;
            Collection<World> worlds = Sponge.getServer().getWorlds();
            for (World world : worlds) {
                Collection<TileEntity> ents = world.getTileEntities(predicate);
                for (TileEntity te : ents) {
                    if (!te.isValid()) continue;
                    entities.add(new CachedTileEntity(te));

                    i++;
                    if (limit > 0 && i >= limit)
                        break;
                }

                if (limit > 0 && i >= limit)
                    break;
            }

            return entities;
        });
    }
    @Override
    public Optional<Collection<ICachedTileEntity>> getTileEntities(ICachedWorld world, Predicate<TileEntity> predicate, int limit) {
        return WebAPI.runOnMain(() -> {
            Optional<?> w = world.getLive();
            if (!w.isPresent())
                return null;

            int i = 0;
            Collection<ICachedTileEntity> entities = new LinkedList<>();
            Collection<TileEntity> ents = ((World)w.get()).getTileEntities(predicate);
            for (TileEntity te : ents) {
                if (!te.isValid()) continue;
                entities.add(new CachedTileEntity(te));

                i++;
                if (limit > 0 && i >= limit)
                    break;
            }

            return entities;
        });
    }
    @Override
    public Optional<ICachedTileEntity> getTileEntity(Location<World> location) {
        Optional<ICachedWorld> w = this.getWorld(location.getExtent().getUniqueId());
        if (!w.isPresent())
            return Optional.empty();

        return getTileEntity(w.get(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    @Override
    public Optional<ICachedTileEntity> getTileEntity(ICachedWorld world, int x, int y, int z) {
        return WebAPI.runOnMain(() -> {
            Optional<?> w = world.getLive();
            if (!w.isPresent())
                return null;

            Optional<TileEntity> ent = ((World)w.get()).getTileEntity(x, y, z);
            if (!ent.isPresent() || !ent.get().isValid()) {
                return null;
            }

            return new CachedTileEntity(ent.get());
        });
    }

    @Override
    public Optional<Object> executeMethod(ICachedObject cache, String methodName, Class[] paramTypes, Object[] paramValues) {
        return WebAPI.runOnMain(() -> {
            Optional<?> obj = cache.getLive();

            if (!obj.isPresent())
                return null;

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
                return new Exception("Method not found");
            }

            try {
                Method m = ms[0];
                m.setAccessible(true);
                Object res = m.invoke(o, paramValues);
                if (m.getReturnType() == Void.class || m.getReturnType() == void.class)
                    return true;
                return res;
            } catch (Exception e) {
                return e;
            }
        });
    }
    @Override
    public Tuple<Map<String, JsonNode>, Map<String, JsonNode>> getExtraData(ICachedObject cache, boolean xml,
                                                                            String[] reqFields, String[] reqMethods) {
        return WebAPI.runOnMain(() -> {
            Map<String, JsonNode> fields = new HashMap<>();
            Map<String, JsonNode> methods = new HashMap<>();

            Optional<?> opt = cache.getLive();
            if (!opt.isPresent()) return null;

            Object obj = opt.get();
            Class c = obj.getClass();

            List<Field> allFields = Arrays.asList(Util.getAllFields(c));
            List<Method> allMethods = Arrays.asList(Util.getAllMethods(c));
            for (String fieldName : reqFields) {
                Optional<Field> field = allFields.stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findAny();
                if (!field.isPresent()) {
                    fields.put(fieldName, TextNode.valueOf("ERROR: Field not found"));
                    continue;
                }

                field.get().setAccessible(true);

                try {
                    Object res = field.get().get(obj);
                    fields.put(fieldName, json.serialize(res, xml, true, IPermissionService.permitAllNode()));
                } catch (IllegalAccessException e) {
                    fields.put(fieldName, TextNode.valueOf("ERROR: " + e.toString()));
                }
            }
            for (String methodName : reqMethods) {
                Optional<Method> method = allMethods.stream().filter(f -> f.getName().equalsIgnoreCase(methodName)).findAny();
                if (!method.isPresent()) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method not found"));
                    continue;
                }

                if (method.get().getParameterCount() > 0) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method must not have parameters"));
                    continue;
                }

                if (method.get().getReturnType().equals(Void.TYPE) || method.get().getReturnType().equals(Void.class)) {
                    methods.put(methodName, TextNode.valueOf("ERROR: Method must not return void"));
                    continue;
                }

                method.get().setAccessible(true);

                try {
                    Object res = method.get().invoke(obj);
                    methods.put(methodName, json.serialize(res, xml, true, IPermissionService.permitAllNode()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    methods.put(methodName, TextNode.valueOf("ERROR: " + e.toString()));
                }
            }

            return new Tuple<>(fields, methods);
        }).orElse(null);
    }
}
