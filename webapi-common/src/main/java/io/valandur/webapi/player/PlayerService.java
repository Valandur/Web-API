package io.valandur.webapi.player;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.logger.Logger;
import jakarta.ws.rs.WebApplicationException;

import java.util.Collection;
import java.util.UUID;

public abstract class PlayerService {

    protected WebAPI<?> webapi;
    protected Logger logger;

    public PlayerService(WebAPI<?> webapi) {
        this.webapi = webapi;
        this.logger = webapi.getLogger();
    }

    public abstract Collection<Player> getPlayers();

    public abstract Player getPlayer(UUID uuid) throws WebApplicationException;

    public abstract PlayerInventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException;

    public abstract void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;
}
