package io.valandur.webapi.player;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import jakarta.ws.rs.WebApplicationException;

import java.util.Collection;
import java.util.UUID;

public abstract class PlayerService<T extends WebAPI<?, ?>> extends Service<T> {

    public PlayerService(T webapi) {
        super(webapi);
    }

    public abstract Collection<Player> getPlayers();

    public abstract Player getPlayer(UUID uuid) throws WebApplicationException;

    public abstract Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException;

    public abstract void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException;

    public abstract void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;
}
