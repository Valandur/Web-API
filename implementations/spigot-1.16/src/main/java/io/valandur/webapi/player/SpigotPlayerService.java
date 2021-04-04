package io.valandur.webapi.player;

import io.valandur.webapi.SpigotWebAPI;
import io.valandur.webapi.item.ItemStack;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotPlayerService extends PlayerService {

    private final Server server;

    public SpigotPlayerService(SpigotWebAPI webapi) {
        super(webapi);

        this.server = webapi.getPlugin().getServer();
    }

    @Override
    public Collection<Player> getPlayers() {
        var players = new ArrayList<Player>();
        for (var player : server.getOnlinePlayers()) {
            players.add(this.toPlayer(player));
        }
        return players;
    }

    @Override
    public Player getPlayer(UUID uuid) throws WebApplicationException {
        var player = server.getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }
        return this.toPlayer(player);
    }

    private Player toPlayer(org.bukkit.entity.Player player) {
        return new Player(
                player.getUniqueId().toString(),
                player.getName(),
                player.getAddress() != null ? player.getAddress().toString() : null
        );
    }

    @Override
    public PlayerInventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
        var player = server.getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemType = type != null ? this.fromType(type) : null;

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
        var slots = itemType != null ? inv.all(itemType).values() : inv;
        for (var stack : slots) {
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
    public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var player = server.getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
        var inv = player.getInventory();
        for (var itemStack : itemStacks) {
            var result = inv.addItem(itemStack);
            if (result.size() > 0) {
                throw new InternalServerErrorException("Could not add item stacks to inventory");
            }
        }
    }

    @Override
    public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {
        var player = server.getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
        var inv = player.getInventory();
        for (var itemStack : itemStacks) {
            var result = inv.removeItem(itemStack);
            if (result.size() > 0) {
                throw new InternalServerErrorException("Could not remove item stacks from inventory");
            }
        }
    }


    private ItemStack toItemStack(org.bukkit.inventory.ItemStack stack) {
        var rawEnchants = stack.getEnchantments();
        var enchantments = new HashMap<String, Integer>(rawEnchants.size());
        for (var entry : rawEnchants.entrySet()) {
            enchantments.put(entry.getKey().getKey().toString(), entry.getValue());
        }

        var meta = new HashMap<String, Object>();
        var stackMeta = stack.getItemMeta();

        String displayName = null;
        Integer damage = null;
        String author = null;
        Collection<String> pages = null;

        if (stackMeta != null) {
            if (stackMeta.hasDisplayName()) {
                displayName = stackMeta.getDisplayName();
            }

            if (stackMeta instanceof BookMeta) {
                var bookMeta = (BookMeta) stackMeta;
                if (bookMeta.hasAuthor()) {
                    author = bookMeta.getAuthor();
                }
                if (bookMeta.hasPages()) {
                    pages = bookMeta.getPages();
                }
            }

            if (stackMeta instanceof Damageable) {
                var damageMeta = (Damageable) stackMeta;
                damage = damageMeta.getDamage();
            }
        }

        return new ItemStack(
                stack.getType().getKey().toString(),
                stack.getAmount(),
                enchantments,
                displayName,
                damage,
                author,
                pages
        );
    }

    private org.bukkit.inventory.ItemStack fromItemStack(ItemStack stack) throws WebApplicationException {
        var material = this.fromType(stack.type);
        return new org.bukkit.inventory.ItemStack(material, stack.amount);
    }

    private Material fromType(String type) {
        var material = Material.getMaterial(type);
        if (material == null) {
            throw new BadRequestException("Invalid item type: " + type);
        }

        return material;
    }
}
