package io.valandur.webapi.forge.player;

import io.valandur.webapi.forge.ForgeWebAPI;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerService;
import jakarta.ws.rs.WebApplicationException;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class ForgePlayerService extends PlayerService<ForgeWebAPI> {

    public ForgePlayerService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<Player> getPlayers() {
        return Collections.emptySet();
    }

    @Override
    public Player getPlayer(UUID uuid) throws WebApplicationException {
        return null;
    }

    @Override
    public Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
        return null;
    }

    @Override
    public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {

    }

    @Override
    public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {

    }

    @Override
    public Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException {
        return null;
    }

    @Override
    public void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {

    }

    @Override
    public void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException {

    }
}
