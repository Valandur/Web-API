package io.valandur.webapi.spigot.player;

import io.valandur.webapi.entity.Location;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.spigot.SpigotWebAPI;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;

public class SpigotPlayerService extends PlayerService<SpigotWebAPI> {

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

  @Override
  public Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
    var player = server.getPlayer(uuid);
    if (player == null) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemType = type != null ? this.fromType(type) : null;

    var inv = player.getInventory();

    var stacks = new ArrayList<ItemStack>();
    var slots = itemType != null ? inv.all(itemType).values() : inv;
    for (var stack : slots) {
      if (stack != null) {
        stacks.add(this.toItemStack(stack));
      }
    }

    return new Inventory(
        inv.getSize(),
        stacks
    );
  }

  @Override
  public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
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
  public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
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

  @Override
  public Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException {
    var player = server.getPlayer(uuid);
    if (player == null) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemType = type != null ? this.fromType(type) : null;

    var inv = player.getEnderChest();

    var stacks = new ArrayList<ItemStack>();
    var slots = itemType != null ? inv.all(itemType).values() : inv;
    for (var stack : slots) {
      if (stack != null) {
        stacks.add(this.toItemStack(stack));
      }
    }

    return new Inventory(
        inv.getSize(),
        stacks
    );
  }

  @Override
  public void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
    var player = server.getPlayer(uuid);
    if (player == null) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
    var inv = player.getEnderChest();
    for (var itemStack : itemStacks) {
      var result = inv.addItem(itemStack);
      if (result.size() > 0) {
        throw new InternalServerErrorException("Could not add item stacks to inventory");
      }
    }
  }

  @Override
  public void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
    var player = server.getPlayer(uuid);
    if (player == null) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
    var inv = player.getEnderChest();
    for (var itemStack : itemStacks) {
      var result = inv.removeItem(itemStack);
      if (result.size() > 0) {
        throw new InternalServerErrorException("Could not remove item stacks from inventory");
      }
    }
  }


  private Player toPlayer(org.bukkit.entity.Player player) {
    var inv = player.getInventory();

    var helmetStack = inv.getHelmet();
    var helmet = helmetStack != null ? this.toItemStack(helmetStack) : null;

    var chestplateStack = inv.getChestplate();
    var chestplate = chestplateStack != null ? this.toItemStack(chestplateStack) : null;

    var leggingsStack = inv.getLeggings();
    var leggings = leggingsStack != null ? this.toItemStack(leggingsStack) : null;

    var bootsStack = inv.getBoots();
    var boots = bootsStack != null ? this.toItemStack(bootsStack) : null;

    return new Player(
        player.getUniqueId().toString(),
        player.getName(),
        player.getWorld().getUID(),
        new Location(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()),
        player.getAddress() != null ? player.getAddress().toString() : null,
        helmet,
        chestplate,
        leggings,
        boots
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

  private org.bukkit.inventory.ItemStack fromItemStack(ItemStack stack)
      throws WebApplicationException {
    var material = this.fromType(stack.type);
    return new org.bukkit.inventory.ItemStack(material, stack.amount);
  }

  private Material fromType(String type) {
    var material = Material.matchMaterial(type);
    if (material == null) {
      throw new BadRequestException("Invalid item type: " + type);
    }

    return material;
  }
}
