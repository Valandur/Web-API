package io.valandur.webapi.player;

import io.valandur.webapi.ForgeWebAPI;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class ForgePlayerService extends PlayerService<ForgeWebAPI> {

    public ForgePlayerService(ForgeWebAPI webapi) {
        super(webapi);
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
    public Player getPlayer(UUID uuid) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }
        return this.toPlayer(player);
    }

    @Override
    public Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemType = type != null ? this.fromType(type) : null;

        var inv = player.inventory;
        int size = inv.getSizeInventory();
        var stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < size; i++) {
            var stack = inv.getStackInSlot(i);
            if (itemType != null && stack.getItem() != itemType) {
                continue;
            }
            if (!stack.isEmpty()) {
                stacks.add(this.toItemStack(stack));
            }
        }

        return new Inventory(
                size,
                stacks
        );
    }

    @Override
    public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());

        var inv = player.inventory;
        for (var itemStack : itemStacks) {
            var success = inv.addItemStackToInventory(itemStack);
            if (!success) {
                throw new InternalServerErrorException("Could not add item stacks to inventory");
            }
            if (!itemStack.isEmpty()) {
                throw new InternalServerErrorException("Could not add item stacks to inventory 2");
            }
        }
    }

    @Override
    public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());

        var inv = player.inventory;
        for (var itemStack : itemStacks) {
            for (int i = 0; i < itemStacks.size(); i++) {
                var stack = inv.getStackInSlot(i);
                if (stack.isEmpty() || stack.getItem() != itemStack.getItem()) {
                    continue;
                }

                var stackCount = stack.getCount();
                var toRemove = Math.min(stackCount, itemStack.getCount());
                stack.setCount(stackCount - toRemove);
                itemStack.setCount(itemStack.getCount() - toRemove);

                if (itemStack.isEmpty()) {
                    break;
                }
            }

            if (!itemStack.isEmpty()) {
                throw new InternalServerErrorException("Could not remove item stacks from inventory");
            }
        }
    }

    @Override
    public Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemType = type != null ? this.fromType(type) : null;

        var inv = player.getInventoryEnderChest();
        int size = inv.getSizeInventory();
        var stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < size; i++) {
            var stack = inv.getStackInSlot(i);
            if (itemType != null && stack.getItem() != itemType) {
                continue;
            }
            if (!stack.isEmpty()) {
                stacks.add(this.toItemStack(stack));
            }
        }

        return new Inventory(
                size,
                stacks
        );
    }

    @Override
    public void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());

        var inv = player.getInventoryEnderChest();
        for (var itemStack : itemStacks) {
            var result = inv.addItem(itemStack);
            if (!result.isEmpty()) {
                throw new InternalServerErrorException("Could not add item stacks to inventory");
            }
        }
    }

    @Override
    public void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var server = ServerLifecycleHooks.getCurrentServer();
        var player = server.getPlayerList().getPlayerByUUID(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());

        var inv = player.getInventoryEnderChest();
        for (var itemStack : itemStacks) {
            for (int i = 0; i < itemStacks.size(); i++) {
                var stack = inv.getStackInSlot(i);
                if (stack.isEmpty() || stack.getItem() != itemStack.getItem()) {
                    continue;
                }

                var stackCount = stack.getCount();
                var toRemove = Math.min(stackCount, itemStack.getCount());
                stack.setCount(stackCount - toRemove);
                itemStack.setCount(itemStack.getCount() - toRemove);

                if (itemStack.isEmpty()) {
                    break;
                }
            }

            if (!itemStack.isEmpty()) {
                throw new InternalServerErrorException("Could not remove item stacks from ender chest");
            }
        }
    }


    private Player toPlayer(ServerPlayerEntity player) {
        var helmetStack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        var helmet = !helmetStack.isEmpty() ? this.toItemStack(helmetStack) : null;

        var chestplateStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        var chestplate = !chestplateStack.isEmpty() ? this.toItemStack(chestplateStack) : null;

        var leggingsStack = player.getItemStackFromSlot(EquipmentSlotType.LEGS);
        var leggings = !leggingsStack.isEmpty() ? this.toItemStack(leggingsStack) : null;

        var bootsStack = player.getItemStackFromSlot(EquipmentSlotType.FEET);
        var boots = !bootsStack.isEmpty() ? this.toItemStack(bootsStack) : null;

        return new Player(
                player.getUniqueID().toString(),
                player.getName().getString(),
                player.getPlayerIP(),
                helmet,
                chestplate,
                leggings,
                boots
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

        String displayName = null;
        Integer damage = null;
        String author = null;
        Collection<String> pages = null;

        if (stack.hasDisplayName()) {
            displayName = stack.getDisplayName().getString();
        }

        var tag = stack.getTag();
        if (tag != null) {
            var pagesTag = tag.get("pages");
            if (pagesTag != null && pagesTag.getType() == ListNBT.TYPE) {
                pages = ((ListNBT) pagesTag).stream().map(INBT::getString).collect(Collectors.toList());
            }

            var authorTag = tag.get("author");
            if (authorTag != null) {
                author = authorTag.getString();
            }

            var damageTag = tag.get("damage");
            if (damageTag != null && damageTag.getType() == IntNBT.TYPE) {
                damage = ((IntNBT) damageTag).getInt();
            }
        }

        var loc = stack.getItem().getRegistryName();
        return new ItemStack(
                loc != null ? loc.toString() : "",
                stack.getCount(),
                enchantments,
                displayName,
                damage,
                author,
                pages
        );
    }

    private net.minecraft.item.ItemStack fromItemStack(ItemStack stack) throws WebApplicationException {
        var type = this.fromType(stack.type);
        return new net.minecraft.item.ItemStack(type, stack.amount);
    }

    private Item fromType(String type) {
        var loc = ResourceLocation.tryCreate(type);
        if (loc == null) {
            throw new BadRequestException("Invalid item type: " + type);
        }

        return ForgeRegistries.ITEMS.getValue(loc);
    }
}
