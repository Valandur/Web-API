package io.valandur.webapi;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerInventory;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WebAPISponge extends WebAPI<SpongeConfig> {

    private final WebAPISpongePlugin plugin;
    private final ExecutorService syncExecutor;

    public WebAPISponge(WebAPISpongePlugin plugin) {
        super();

        this.plugin = plugin;
        this.syncExecutor = Sponge.server().scheduler().createExecutor(plugin.getContainer());
    }

    @Override
    public Collection<Player> getPlayers() {
        var players = new ArrayList<Player>();
        for (var player : Sponge.server().onlinePlayers()) {
            players.add(this.toPlayer(player));
        }
        return players;
    }

    @Override
    public Player getPlayer(UUID uuid) throws WebApplicationException {
        var player = Sponge.server().player(uuid);
        if (player.isEmpty()) {
            throw new NotFoundException("Player not found: " + uuid);
        }
        return this.toPlayer(player.get());
    }

    private Player toPlayer(org.spongepowered.api.entity.living.player.server.ServerPlayer player) {
        return new Player(
                player.uniqueId().toString(),
                player.name(),
                player.connection().address().toString()
        );
    }

    @Override
    public PlayerInventory getPlayerInventory(UUID uuid) throws WebApplicationException {
        var player = Sponge.server().player(uuid);
        if (player.isEmpty()) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var inv = player.get().inventory();
        var armor = inv.armor();

        var helmet = armor.peek(EquipmentTypes.HEAD)
                .map(stack -> !stack.isEmpty() ? this.toItemStack(stack) : null)
                .orElse(null);

        var chestplate = armor.peek(EquipmentTypes.CHEST)
                .map(stack -> !stack.isEmpty() ? this.toItemStack(stack) : null)
                .orElse(null);

        var leggings = armor.peek(EquipmentTypes.LEGS)
                .map(stack -> !stack.isEmpty() ? this.toItemStack(stack) : null)
                .orElse(null);

        var boots = armor.peek(EquipmentTypes.FEET)
                .map(stack -> !stack.isEmpty() ? this.toItemStack(stack) : null)
                .orElse(null);

        var stacks = new ArrayList<ItemStack>();
        for (Inventory slot : inv.slots()) {
            var item = slot.peek();
            if (!item.isEmpty()) {
                stacks.add(this.toItemStack(item));
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
    public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var player = Sponge.server().player(uuid);
        if (player.isEmpty()) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
        var inv = player.get().inventory();

        for (var itemStack : itemStacks) {
            var result = inv.offer(itemStack);
            if (result.type() != InventoryTransactionResult.Type.SUCCESS) {
                throw new InternalServerErrorException("Could not add item stacks to inventory");
            }
        }
    }

    @Override
    public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        throw new InternalServerErrorException("Method not implemented");
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        for (var world : Sponge.server().worldManager().worlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
    }

    private World toWorld(org.spongepowered.api.world.server.ServerWorld world) {
        var props = world.properties();
        var gameRules = new ArrayList<GameRule>();
        for (var gameRule : props.gameRules().entrySet()) {
            Object value = null;
            var rawValue = gameRule.getValue().toString();
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
            gameRules.add(new GameRule(gameRule.getKey().toString(), value));
        }

        return new World(
                world.uniqueId().toString(),
                props.displayName().map(Object::toString).orElse(null),
                world.properties().worldType().toString(),
                world.difficulty().toString(),
                world.seed(),
                gameRules
        );
    }

    @Override
    public ServerInfo getInfo() {
        var server = Sponge.server();

        return new ServerInfo(
                SpongeComponents.plainSerializer().serialize(server.motd()),
                server.onlinePlayers().size(),
                server.maxPlayers(),
                server.isOnlineModeEnabled(),
                plugin.getUptime(),
                Sponge.platform().minecraftVersion().name()
        );
    }

    private ItemStack toItemStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        var enchantments = new HashMap<String, Integer>();
        stack.get(Keys.APPLIED_ENCHANTMENTS).ifPresent(enchantmentData -> {
            for (var enchantment : enchantmentData) {
                enchantments.put(enchantment.type().key(RegistryTypes.ENCHANTMENT_TYPE).asString(),
                        enchantment.level());
            }
        });

        var meta = new HashMap<String, Object>();
        stack.get(Keys.PAGES).ifPresent(pages -> {
            meta.put("pages",
                    pages.stream().map(page -> SpongeComponents.plainSerializer().serialize(page)).toArray());
        });
        stack.get(Keys.PLAIN_PAGES).ifPresent(pages -> {
            meta.put("pages", pages);
        });
        stack.get(Keys.AUTHOR).ifPresent(author -> {
            meta.put("author", SpongeComponents.plainSerializer().serialize(author));
        });

        if (stack.supports(Keys.DISPLAY_NAME)) {
            stack.get(Keys.DISPLAY_NAME).ifPresent(displayName -> {
                meta.put("displayName", SpongeComponents.plainSerializer().serialize(displayName));
            });
        }

        return new ItemStack(
                stack.type().key(RegistryTypes.ITEM_TYPE).asString(),
                stack.quantity(),
                meta,
                enchantments
        );
    }

    private org.spongepowered.api.item.inventory.ItemStack fromItemStack(ItemStack stack) throws WebApplicationException {
        var key = ResourceKey.resolve(stack.type);
        var entry = RegistryTypes.ITEM_TYPE.get().findEntry(key);
        if (entry.isEmpty()) {
            throw new BadRequestException("Invalid item: " + stack.type);
        }

        return org.spongepowered.api.item.inventory.ItemStack.of(entry.get().value(), stack.amount);
    }


    @Override
    public SpongeConfig getConfig(String name) {
        var confName = name + ".conf";
        return new SpongeConfig(confName, plugin.getConfigPath().resolve(confName));
    }


    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        if (Sponge.server().onMainThread()) {
            runnable.run();
        } else {
            var future = CompletableFuture.runAsync(runnable, syncExecutor);
            future.get();
        }
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        if (Sponge.server().onMainThread()) {
            return supplier.get();
        } else {
            var future = CompletableFuture.supplyAsync(supplier, syncExecutor);
            return future.get();
        }
    }
}
