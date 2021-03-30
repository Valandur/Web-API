package io.valandur.webapi;

import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerInventory;
import io.valandur.webapi.user.User;
import io.valandur.webapi.world.GameRule;
import io.valandur.webapi.world.World;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WebAPIForge extends WebAPI<ForgeConfig> {

    private final WebAPIForgePlugin plugin;

    public WebAPIForge(WebAPIForgePlugin plugin) {
        super();

        this.plugin = plugin;
    }


    @Override
    public Collection<User> getUsers() {
        return null;
    }

    @Override
    public User getUser(UUID uuid) {
        return null;
    }

    @Override
    public Collection<Player> getPlayers() {
        var players = new ArrayList<Player>();
        var server = ServerLifecycleHooks.getCurrentServer();
        for (var player : server.getPlayerList().getPlayers()) {
            players.add(this.toPlayer(player));
        }
        return players;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        return player != null ? this.toPlayer(player) : null;
    }

    private Player toPlayer(ServerPlayerEntity player) {
        return new Player(
                player.getUniqueID().toString(),
                player.getName().getString(),
                player.getPlayerIP()
        );
    }


    @Override
    public PlayerInventory getPlayerInventory(UUID uuid) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            return null;
        }

        var helmetStack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        var helmet = !helmetStack.isEmpty() ? this.toItemStack(helmetStack) : null;

        var chestplateStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        var chestplate = !chestplateStack.isEmpty() ? this.toItemStack(chestplateStack) : null;

        var leggingsStack = player.getItemStackFromSlot(EquipmentSlotType.LEGS);
        var leggings = !leggingsStack.isEmpty() ? this.toItemStack(leggingsStack) : null;

        var bootsStack = player.getItemStackFromSlot(EquipmentSlotType.FEET);
        var boots = !bootsStack.isEmpty() ? this.toItemStack(bootsStack) : null;

        int size = player.inventory.getSizeInventory();
        var stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < size; i++) {
            var stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
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
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            return;
        }

        var itemStack = this.fromItemStack(stack);
        if (itemStack == null) {
            return;
        }

        player.addItemStackToInventory(itemStack);
    }

    @Override
    public Collection<World> getWorlds() {
        var worlds = new ArrayList<World>();
        var server = ServerLifecycleHooks.getCurrentServer();
        for (var world : server.getWorlds()) {
            worlds.add(this.toWorld(world));
        }
        return worlds;
    }

    private World toWorld(net.minecraft.world.server.ServerWorld world) {
        var gameRules = new ArrayList<GameRule>();
        var rules = world.getGameRules();
        GameRules.visitAll(new GameRules.IRuleEntryVisitor() {
            @Override
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
                gameRules.add(new GameRule(key.getName(), rules.get(key).stringValue()));
            }
        });

        return new World(
                "",
                ((IServerWorldInfo) world.getWorldInfo()).getWorldName(),
                world.getDimensionKey().getLocation().toString(),
                world.getDifficulty().name(),
                world.getSeed(),
                gameRules
        );
    }

    @Override
    public ServerInfo getInfo() {
        var server = ServerLifecycleHooks.getCurrentServer();

        return new ServerInfo(
                server.getMOTD(),
                server.getPlayerList().getCurrentPlayerCount(),
                server.getMaxPlayers(),
                server.isServerInOnlineMode(),
                plugin.getUptime(),
                server.getMinecraftVersion()
        );
    }


    private ItemStack toItemStack(net.minecraft.item.ItemStack stack) {
        var rawEnchants = EnchantmentHelper.getEnchantments(stack);
        var enchantments = new HashMap<String, Integer>(rawEnchants.size());
        for (var entry : rawEnchants.entrySet()) {
            var loc = entry.getKey().getRegistryName();
            if (loc != null) {
                enchantments.put(loc.toString(), entry.getValue());
            }
        }

        var meta = new HashMap<String, Object>();

        if (stack.hasDisplayName()) {
            meta.put("displayName", stack.getDisplayName());
        }

        var tag = stack.getTag();
        if (tag != null) {
            var pages = tag.getList("pages", 8); // 8 = StringNBT type id
            if (!pages.isEmpty()) {
                meta.put("pages",
                        pages.stream().map(INBT::getString).collect(Collectors.toList()));
            }
            var authorTag = tag.get("author");
            if (authorTag != null) {
                meta.put("author", authorTag.getString());
            }
            var titleTag = tag.get("title");
            if (titleTag != null) {
                meta.put("title", titleTag.getString());
            }
        }

        var loc = stack.getItem().getRegistryName();
        return new ItemStack(
                loc != null ? loc.toString() : "",
                stack.getCount(),
                meta,
                enchantments
        );
    }

    private net.minecraft.item.ItemStack fromItemStack(ItemStack stack) {
        var loc = ResourceLocation.tryCreate(stack.type);
        if (loc == null) {
            return null;
        }

        var item = ForgeRegistries.ITEMS.getValue(loc);
        return new net.minecraft.item.ItemStack(item, stack.amount);
    }


    @Override
    public ForgeConfig getConfig(String name) {
        return new ForgeConfig(name);
    }


    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        ServerLifecycleHooks.getCurrentServer().runAsync(runnable);
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        var result = new ArrayList<T>(1);
        ServerLifecycleHooks.getCurrentServer().runAsync(() -> result.add(supplier.get())).get();
        return result.get(0);
    }
}
