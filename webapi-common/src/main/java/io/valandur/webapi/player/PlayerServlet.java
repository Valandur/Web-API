package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.BaseServlet;
import io.valandur.webapi.item.ItemStack;
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
        return webapi.runOnMain(() -> webapi.getPlayers());
    }

    @GET
    @Path("{player}")
    @GraphQLQuery(name = "player")
    public Player getPlayer(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> webapi.getPlayer(uuid));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @GET
    @Path("{player}/inventory")
    @GraphQLQuery(name = "playerInventory")
    public PlayerInventory getPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> webapi.getPlayerInventory(uuid));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @POST
    @Path("{player}/inventory")
    @GraphQLMutation(name = "addToPlayerInventory")
    public void addToPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to add") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> webapi.addToPlayerInventory(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }

    @DELETE
    @Path("{player}/inventory")
    @GraphQLMutation(name = "removeFromPlayerInventory")
    public void removeFromPlayerInventory(
            @PathParam("player") @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
            @GraphQLArgument(name = "itemStacks", description = "The item stacks to remove") Collection<ItemStack> stacks)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            webapi.runOnMain(() -> webapi.removeFromPlayerInventory(uuid, stacks));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid " + uuidString);
        }
    }
}
