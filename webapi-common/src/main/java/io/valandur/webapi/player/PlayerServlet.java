package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.security.Access;
import io.valandur.webapi.security.AccessControl;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerServlet extends BaseServlet {

    @GET
    @GraphQLQuery(name = "players")
    public Collection<Player> getPlayers() throws ExecutionException, InterruptedException {
        return webapi.runOnMain(() -> playerService.getPlayers());
    }

    @GET
    @Path("{player}")
    @GraphQLQuery(name = "player")
    public Player getPlayer(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> playerService.getPlayer(uuid));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @GET
    @Path("{player}/inventory")
    @GraphQLQuery(name = "playerInventory")
    public Inventory getPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @QueryParam("type") @GraphQLArgument(name = "type", description = "The type of item to filter") String type)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> playerService.getPlayerInventory(uuid, type));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @POST
    @Path("{player}/inventory")
    @GraphQLMutation(name = "addToPlayerInventory")
    @AccessControl(Access.WRITE)
    public void addToPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to add") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> playerService.addToPlayerInventory(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @DELETE
    @Path("{player}/inventory")
    @GraphQLMutation(name = "removeFromPlayerInventory")
    @AccessControl(Access.WRITE)
    public void removeFromPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to remove") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> playerService.removeFromPlayerInventory(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }


    @GET
    @Path("{player}/ender-chest")
    @GraphQLQuery(name = "playerEnderChest")
    public Inventory getPlayerEnderChest(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @QueryParam("type") @GraphQLArgument(name = "type", description = "The type of item to filter") String type)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> playerService.getPlayerEnderChest(uuid, type));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @POST
    @Path("{player}/ender-chest")
    @GraphQLMutation(name = "addToPlayerEnderChest")
    @AccessControl(Access.WRITE)
    public void addToPlayerEnderChest(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to add") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> playerService.addToPlayerEnderChest(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @DELETE
    @Path("{player}/ender-chest")
    @GraphQLMutation(name = "removeFromPlayerEnderChest")
    @AccessControl(Access.WRITE)
    public void removeFromPlayerEnderChest(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to remove") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> playerService.removeFromPlayerEnderChest(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }
}
