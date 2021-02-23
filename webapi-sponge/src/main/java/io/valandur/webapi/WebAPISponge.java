package io.valandur.webapi;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.user.User;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.PagedData;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WebAPISponge extends WebAPI<SpongeConfig> {

    private final WebAPISpongePlugin plugin;

    public WebAPISponge(WebAPISpongePlugin plugin) {
        super();

        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }


    @Override
    public Collection<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        if (userStorage.isEmpty()) {
            return users;
        }

        for (GameProfile profile : userStorage.get().getAll()) {
            users.add(new User(profile.getUniqueId().toString(), profile.getName().orElse(null)));
        }
        return users;
    }

    @Override
    public Collection<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (org.spongepowered.api.entity.living.player.Player player : Sponge.getServer().getOnlinePlayers()) {
            final ItemStack[] equipment = {null, null, null, null};
            player.getHelmet().ifPresent((stack) -> equipment[0] = this.toItemStack(stack));
            player.getChestplate().ifPresent((stack) -> equipment[1] = this.toItemStack(stack));
            player.getLeggings().ifPresent((stack) -> equipment[2] = this.toItemStack(stack));
            player.getBoots().ifPresent((stack) -> equipment[3] = this.toItemStack(stack));

            CarriedInventory<? extends Carrier> inv = player.getInventory();
            Collection<ItemStack> stacks = new ArrayList<>(inv.totalItems());
            for (Inventory slot : inv.slots()) {
                slot.peek().ifPresent(stack -> stacks.add(this.toItemStack(stack)));
            }

            players.add(new Player(player.getIdentifier(), player.getName(),
                    player.getConnection().getAddress().toString(), equipment[0], equipment[1], equipment[2],
                    equipment[3], stacks));
        }
        return players;
    }

    @Override
    public Collection<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<>();
        for (org.spongepowered.api.world.World world : Sponge.getServer().getWorlds()) {
            WorldProperties props = world.getProperties();
            Collection<GameRule> gameRules = new ArrayList<>(props.getGameRules().size());
            for (Map.Entry<String, String> gameRule : props.getGameRules().entrySet()) {
                Object value = null;
                String rawValue = gameRule.getValue();
                if (rawValue.equalsIgnoreCase("true")) {
                    value = true;
                } else if (rawValue.equalsIgnoreCase("false")) {
                    value = false;
                }
                if (value == null) {
                    try {
                        value = Integer.parseInt(rawValue);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (value == null) {
                    try {
                        value = Double.parseDouble(rawValue);
                    } catch (NumberFormatException ignored) {
                    }
                }
                gameRules.add(new GameRule(gameRule.getKey(), value));
            }
            worlds.add(new World(world.getUniqueId().toString(), world.getName(),
                    world.getDimension().getType().getName(), world.getDifficulty().getName(), props.getSeed(),
                    gameRules));
        }
        return worlds;
    }


    @Override
    public ServerInfo getInfo() {
        Server server = Sponge.getServer();

        return new ServerInfo(
                server.getMotd().toPlainSingle(),
                server.getOnlinePlayers().size(),
                server.getMaxPlayers(),
                server.getBoundAddress().map(InetSocketAddress::toString).orElse(null),
                server.getOnlineMode(),
                plugin.getUptime(),
                server.getTicksPerSecond(),
                Sponge.getPlatform().getMinecraftVersion().getName()
        );
    }


    @Override
    public SpongeConfig getConfig(String name) {
        String confName = name + ".conf";
        return new SpongeConfig(confName, plugin.getConfigPath().resolve(confName));
    }

    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        plugin.runOnMain(runnable);
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        return plugin.runOnMain(supplier);
    }

    private ItemStack toItemStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        Map<String, Integer> enchantments = new HashMap<>();
        stack.get(EnchantmentData.class).ifPresent(enchantmentData -> {
            enchantmentData.enchantments().forEach(enchantment -> {
                enchantments.put(enchantment.getType().getId(), enchantment.getLevel());
            });
        });

        List<String> lore = stack.get(Keys.ITEM_LORE)
                .map(rawLore -> rawLore.stream().map(Text::toPlainSingle).collect(Collectors.toList()))
                .orElse(null);

        Map<String, Object> meta = new HashMap<>();
        stack.get(PagedData.class).ifPresent(pagedData -> {
            meta.put("pages", pagedData.pages().get().stream().map(Text::toPlainSingle).toArray(String[]::new));
        });
        stack.get(AuthorData.class).ifPresent(authorData -> {
            meta.put("author", authorData.author().get().toPlainSingle());
        });
        stack.get(DisplayNameData.class).ifPresent(displayNameData -> {
            meta.put("displayName", displayNameData.displayName().get().toPlainSingle());
        });

        try {
            getLogger().info(DataFormats.JSON.write(stack.toContainer()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ItemStack(stack.getType().getName(), stack.getQuantity(), meta, enchantments, lore);
    }
}
