package io.valandur.webapi;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.user.User;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class WebAPIPaper extends WebAPI<PaperConfig> {

    private final WebAPIPaperPlugin plugin;

    public WebAPIPaper(WebAPIPaperPlugin plugin) {
        super();

        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        return plugin.getSLF4JLogger();
    }


    @Override
    public Collection<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
            users.add(new User(player.getUniqueId().toString(), player.getName()));
        }
        return users;
    }

    @Override
    public Collection<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            org.bukkit.inventory.ItemStack[] equipment = {inv.getHelmet(), inv.getChestplate(), inv.getLeggings(),
                    inv.getBoots()};

            ItemStack helmet = null;
            if (equipment[0] != null) {
                helmet = this.toItemStack(equipment[0]);
            }
            ItemStack chestplate = null;
            if (equipment[1] != null) {
                chestplate = this.toItemStack(equipment[1]);
            }
            ItemStack leggings = null;
            if (equipment[2] != null) {
                leggings = this.toItemStack(equipment[2]);
            }
            ItemStack boots = null;
            if (equipment[3] != null) {
                boots = this.toItemStack(equipment[3]);
            }

            Collection<ItemStack> stacks = new ArrayList<>(inv.getSize());
            for (org.bukkit.inventory.ItemStack stack : inv) {
                if (stack != null) {
                    stacks.add(this.toItemStack(stack));
                }
            }

            players.add(new Player(player.getUniqueId().toString(), player.getName(), player.getAddress().toString(),
                    helmet, chestplate, leggings, boots, stacks));
        }
        return players;
    }

    @Override
    public Collection<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<>();
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            String[] gameRuleNames = world.getGameRules();
            Collection<GameRule> gameRules = new ArrayList<>(gameRuleNames.length);
            for (String gameRuleName : gameRuleNames) {
                org.bukkit.GameRule<?> gameRule = org.bukkit.GameRule.getByName(gameRuleName);
                if (gameRule != null) {
                    Object value = world.getGameRuleValue(gameRule);
                    gameRules.add(new GameRule(gameRule.getName(), value));
                }
            }
            worlds.add(new World(world.getUID().toString(), world.getName(), world.getEnvironment().name(),
                    world.getDifficulty().name(), world.getSeed(), gameRules));
        }
        return worlds;
    }


    @Override
    public ServerInfo getInfo() {
        Server server = plugin.getServer();
        return new ServerInfo(
                server.getMotd(),
                server.getOnlinePlayers().size(),
                server.getMaxPlayers(),
                server.getIp(),
                server.getOnlineMode(),
                plugin.getUptime(),
                server.getTPS()[0],
                server.getMinecraftVersion()
        );
    }


    @Override
    public PaperConfig getConfig(String name) {
        return new PaperConfig(name + ".yml", plugin);
    }

    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        plugin.runOnMain(runnable);
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        return plugin.runOnMain(supplier);
    }

    private ItemStack toItemStack(org.bukkit.inventory.ItemStack stack) {
        Map<Enchantment, Integer> rawEnchants = stack.getEnchantments();
        Map<String, Integer> enchantments = new HashMap<>(rawEnchants.size());
        for (Map.Entry<Enchantment, Integer> entry : rawEnchants.entrySet()) {
            enchantments.put(entry.getKey().getKey().toString(), entry.getValue());
        }

        Map<String, Object> meta = new HashMap<>();
        ItemMeta stackMeta = stack.getItemMeta();

        if (stackMeta.hasDisplayName()) {
            meta.put("displayName", stackMeta.getDisplayName());
        }

        if (stackMeta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) stackMeta;
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

        return new ItemStack(stack.getType().getKey().toString(), stack.getAmount(), meta, enchantments,
                stack.getLore());
    }
}
