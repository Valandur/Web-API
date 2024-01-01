package io.valandur.webapi.fabric.player;

import io.valandur.webapi.world.Location;
import io.valandur.webapi.fabric.FabricWebAPI;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.world.Position;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.minecraft.server.network.ServerPlayerEntity;

public class FabricPlayerService extends PlayerService<FabricWebAPI> {

    public FabricPlayerService(FabricWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<Player> getPlayers() {
        var rawPlayers = webapi.getPlugin().getServer().getPlayerManager().getPlayerList();
        var players = new ArrayList<Player>(rawPlayers.size());
        for (var rawPlayer : rawPlayers) {
            players.add(toPlayer(rawPlayer));
        }
        return players;
    }

    @Override
    public Player getPlayer(UUID uuid) throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }
        return toPlayer(player);
    }

    @Override
    public Inventory getPlayerInventory(UUID uuid, String type) throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        return null;
    }

    @Override
    public void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
            throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

    }

    @Override
    public void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks)
            throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

    }

    @Override
    public Inventory getPlayerEnderChest(UUID uuid, String type) throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

        return null;
    }

    @Override
    public void addToPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
            throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

    }

    @Override
    public void removeFromPlayerEnderChest(UUID uuid, Collection<ItemStack> stacks)
            throws WebApplicationException {
        var player = webapi.getPlugin().getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            throw new NotFoundException("Player not found: " + uuid);
        }

    }

    private Player toPlayer(ServerPlayerEntity player) {
        return new Player(
                player.getUuid(),
                player.getName().toString(),
                new Location(
                        webapi.getWorldUUID(player.getServerWorld()),
                        new Position(player.getX(), player.getY(), player.getZ())
                ),
                player.getIp(),
                null,
                null,
                null,
                null
        );
    }
}
