package io.valandur.webapi.sponge.player;

import io.valandur.webapi.entity.Location;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.sponge.SpongeWebAPI;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.registry.RegistryTypes;

public class SpongePlayerService extends PlayerService<SpongeWebAPI> {

  public SpongePlayerService(SpongeWebAPI webapi) {
    super(webapi);
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

  @Override
  public Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
    var player = Sponge.server().player(uuid);
    if (player.isEmpty()) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemType = type != null ? this.fromType(type) : null;

    var inv = player.get().inventory();

    var stacks = new ArrayList<ItemStack>();
    var slots = (itemType != null ? inv.query(QueryTypes.ITEM_TYPE.get().of(itemType))
        : inv).slots();
    for (org.spongepowered.api.item.inventory.Inventory slot : slots) {
      var item = slot.peek();
      if (!item.isEmpty()) {
        stacks.add(this.toItemStack(item));
      }
    }

    return new Inventory(
        inv.capacity(),
        stacks
    );
  }

  @Override
  public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
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
  public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
    var player = Sponge.server().player(uuid);
    if (player.isEmpty()) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
    var inv = player.get().inventory();

    for (var itemStack : itemStacks) {
      var result =
          inv.query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(itemStack))
              .poll(itemStack.quantity());
      if (result.type() != InventoryTransactionResult.Type.SUCCESS) {
        throw new InternalServerErrorException("Could not remove item stacks from inventory");
      }
    }
  }

  @Override
  public Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException {
    var player = Sponge.server().player(uuid);
    if (player.isEmpty()) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemType = type != null ? this.fromType(type) : null;

    var inv = player.get().enderChestInventory();

    var stacks = new ArrayList<ItemStack>();
    var slots = (itemType != null ? inv.query(QueryTypes.ITEM_TYPE.get().of(itemType))
        : inv).slots();
    for (org.spongepowered.api.item.inventory.Inventory slot : slots) {
      var item = slot.peek();
      if (!item.isEmpty()) {
        stacks.add(this.toItemStack(item));
      }
    }

    return new Inventory(
        inv.capacity(),
        stacks
    );
  }

  @Override
  public void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
    var player = Sponge.server().player(uuid);
    if (player.isEmpty()) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
    var inv = player.get().enderChestInventory();

    for (var itemStack : itemStacks) {
      var result = inv.offer(itemStack);
      if (result.type() != InventoryTransactionResult.Type.SUCCESS) {
        throw new InternalServerErrorException("Could not add item stacks to inventory");
      }
    }
  }

  @Override
  public void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
      throws WebApplicationException {
    var player = Sponge.server().player(uuid);
    if (player.isEmpty()) {
      throw new NotFoundException("Player not found: " + uuid);
    }

    var itemStacks = stacks.stream().map(this::fromItemStack).collect(Collectors.toList());
    var inv = player.get().enderChestInventory();

    for (var itemStack : itemStacks) {
      var result =
          inv.query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(itemStack))
              .poll(itemStack.quantity());
      if (result.type() != InventoryTransactionResult.Type.SUCCESS) {
        throw new InternalServerErrorException("Could not remove item stacks from inventory");
      }
    }
  }


  private Player toPlayer(org.spongepowered.api.entity.living.player.server.ServerPlayer player) {
    var armor = player.inventory().armor();

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

    return new Player(
        player.uniqueId().toString(),
        player.name(),
        player.world().uniqueId(),
        new Location(player.location().x(), player.location().y(), player.location().z()),
        player.connection().address().toString(),
        helmet,
        chestplate,
        leggings,
        boots
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
    stack.get(Keys.STORED_ENCHANTMENTS).ifPresent(enchantmentData -> {
      for (var enchantment : enchantmentData) {
        enchantments.put(enchantment.type().key(RegistryTypes.ENCHANTMENT_TYPE).asString(),
            enchantment.level());
      }
    });

    var pages = stack.get(Keys.PAGES)
        .map(rawPages ->
            rawPages.stream()
                .map(page -> PlainTextComponentSerializer.plainText().serialize(page))
                .collect(Collectors.toList()))
        .orElse(null);

    if (pages == null) {
      pages = stack.get(Keys.PLAIN_PAGES).orElse(null);
    }

    var author = stack.get(Keys.AUTHOR)
        .map(rawAuthor -> PlainTextComponentSerializer.plainText().serialize(rawAuthor))
        .orElse(null);

    var displayName = stack.get(Keys.DISPLAY_NAME)
        .map(rawDisplayName -> PlainTextComponentSerializer.plainText().serialize(rawDisplayName))
        .orElse(null);

    var damage = stack.get(Keys.ITEM_DURABILITY).orElse(null);

    return new ItemStack(
        stack.type().key(RegistryTypes.ITEM_TYPE).asString(),
        stack.quantity(),
        enchantments,
        displayName,
        damage,
        author,
        pages
    );
  }

  private org.spongepowered.api.item.inventory.ItemStack fromItemStack(ItemStack stack)
      throws WebApplicationException {
    var type = this.fromType(stack.type);
    return org.spongepowered.api.item.inventory.ItemStack.of(type, stack.amount);
  }

  private ItemType fromType(String type) {
    var key = ResourceKey.resolve(type);
    var entry = RegistryTypes.ITEM_TYPE.get().findEntry(key);
    if (entry.isEmpty()) {
      throw new BadRequestException("Invalid item type: " + type);
    }

    return entry.get().value();
  }
}
