package io.valandur.webapi;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerInventory;
import io.valandur.webapi.user.User;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class WebAPISpigot extends WebAPI<SpigotConfig> {

    private final WebAPISpigotPlugin plugin;

    public WebAPISpigot(WebAPISpigotPlugin plugin) {
        super();

        this.plugin = plugin;
    }


    @Override
    public Collection<User> getUsers() {
        var users = new ArrayList<User>();
        for (var user : plugin.getServer().getOfflinePlayers()) {
            users.add(this.toUser(user));
        }
        return users;
    }

    @Override
    public User getUser(UUID uuid) {
        var user = plugin.getServer().getOfflinePlayer(uuid);
        return this.toUser(user);
    }

    private User toUser(OfflinePlayer user) {
        return new User(user.getUniqueId().toString(), user.getName());
    }

    @Override
    public Collection<Player> getPlayers() {
        var players = new ArrayList<Player>();
        for (var player : plugin.getServer().getOnlinePlayers()) {
            players.add(this.toPlayer(player));
        }
        return players;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        var player = plugin.getServer().getPlayer(uuid);
        return player == null ? null : this.toPlayer(player);
    }

    private Player toPlayer(org.bukkit.entity.Player player) {
        return new Player(
                player.getUniqueId().toString(),
                player.getName(),
                player.getAddress() != null ? player.getAddress().toString() : null
        );
    }

    @Override
    public io.valandur.webapi.player.PlayerInventory getPlayerInventory(UUID uuid) {
        var player = plugin.getServer().getPlayer(uuid);
        if (player == null) {
            return null;
        }

        var inv = player.getInventory();

        var helmetStack = inv.getHelmet();
        var helmet = helmetStack != null ? this.toItemStack(helmetStack) : null;

        var chestplateStack = inv.getChestplate();
        var chestplate = chestplateStack != null ? this.toItemStack(chestplateStack) : null;

        var leggingsStack = inv.getLeggings();
        var leggings = leggingsStack != null ? this.toItemStack(leggingsStack) : null;
        
        var bootsStack = inv.getBoots();
        var boots = bootsStack != null ? this.toItemStack(bootsStack) : null;

        var stacks = new ArrayList<ItemStack>();
        for (var stack : inv) {
            if (stack != null) {
                stacks.add(this.toItemStack(stack));
            }
        }

        return new PlayerInventory(
                helmet,
                chestplate,
                leggings,
                boots,
                stacks
        );
    }

    @Override
    public void addToPlayerInventory(UUID uuid, ItemStack stack) {
        var player = plugin.getServer().getPlayer(uuid);
        if (player == null) {
            return;
        }

        var itemStack = this.fromItemStack(stack);
        if (itemStack == null) {
            return;
        }

        var inv = player.getInventory();
        inv.addItem(itemStack);
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        for (var world : plugin.getServer().getWorlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
    }

    private World toWorld(org.bukkit.World world) {
        var gameRuleNames = world.getGameRules();
        var gameRules = new ArrayList<GameRule>();
        for (var gameRuleName : gameRuleNames) {
            var gameRule = org.bukkit.GameRule.getByName(gameRuleName);
            if (gameRule != null) {
                var value = world.getGameRuleValue(gameRule);
                gameRules.add(new GameRule(gameRule.getName(), value));
            }
        }

        return new World(
                world.getUID().toString(),
                world.getName(),
                world.getEnvironment().name(),
                world.getDifficulty().name(),
                world.getSeed(),
                gameRules
        );
    }

    @Override
    public ServerInfo getInfo() {
        var server = plugin.getServer();
        return new ServerInfo(
                server.getMotd(),
                server.getOnlinePlayers().size(),
                server.getMaxPlayers(),
                server.getOnlineMode(),
                plugin.getUptime(),
                server.getVersion()
        );
    }

    private ItemStack toItemStack(org.bukkit.inventory.ItemStack stack) {
        var rawEnchants = stack.getEnchantments();
        var enchantments = new HashMap<String, Integer>(rawEnchants.size());
        for (var entry : rawEnchants.entrySet()) {
            enchantments.put(entry.getKey().getKey().toString(), entry.getValue());
        }

        var meta = new HashMap<String, Object>();
        var stackMeta = stack.getItemMeta();

        if (stackMeta != null) {
            if (stackMeta.hasDisplayName()) {
                meta.put("displayName", stackMeta.getDisplayName());
            }

            if (stackMeta instanceof BookMeta) {
                var bookMeta = (BookMeta) stackMeta;
                if (bookMeta.hasAuthor()) {
                    meta.put("author", bookMeta.getAuthor());
                }
                if (bookMeta.hasPages()) {
                    meta.put("pages", bookMeta.getPages());
                }
                if (bookMeta.hasTitle()) {
                    meta.put("title", bookMeta.getTitle());
                }
            }
        }

        return new ItemStack(
                stack.getType().getKey().toString(),
                stack.getAmount(),
                meta,
                enchantments
        );
    }

    private org.bukkit.inventory.ItemStack fromItemStack(ItemStack stack) {
        var material = Material.getMaterial(stack.type);
        if (material == null) {
            return null;
        }
        return new org.bukkit.inventory.ItemStack(material, stack.amount);
    }


    @Override
    public SpigotConfig getConfig(String name) {
        return new SpigotConfig(name + ".yml", plugin);
    }


    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        var future = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
            runnable.run();
            return null;
        });
        future.get();
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        var future = plugin.getServer().getScheduler().callSyncMethod(plugin, supplier::get);
        return future.get();
    }
}
