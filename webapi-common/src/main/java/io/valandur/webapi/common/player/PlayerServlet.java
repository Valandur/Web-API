package io.valandur.webapi.common.player;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.common.security.AccessControl;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.item.Inventory;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.security.Access;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("player")
@Tag(name = "Player", description = PlayerServlet.classDescr)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerServlet extends BaseServlet {

  protected static final String classDescr = "Get & modify players and their inventories";
  private static final String getPlayersDescr = "Gets a list of currently online players";
  private static final String getPlayerDescr = "Gets a currently online player by UUID";
  private static final String getPlayerInventoryDescr = "Gets the inventory of a currently online player";
  private static final String addToPlayerInventoryDescr = "Adds the provided item stacks to the first free slots within the player's inventory";
  private static final String removeFromPlayerInventoryDescr = "Removes the provided item stacks from the player's inventory";
  private static final String getPlayerEnderChestDescr = "Gets the contents of the player's ender chest";
  private static final String addToPlayerEnderChestDescr = "Adds the provided item stacks to the first free slots within the player's ender chest";
  private static final String removeFromPlayerEnderChestDescr = "Removes the provided item stacks from the player's ender chest";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "players", description = getPlayersDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An array of currently online players",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Player.class))))
  public Collection<Player> getPlayers() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> playerService.getPlayers());
  }

  @GET
  @Path("{player}")
  @GraphQLNonNull
  @GraphQLQuery(name = "player", description = getPlayerDescr)
  @ApiResponse(
      responseCode = "200",
      description = "A currently online player",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Player.class)))
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  public Player getPlayer(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString)
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
  @GraphQLNonNull
  @GraphQLQuery(name = "playerInventory", description = getPlayerInventoryDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The inventory of a currently online player",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Inventory.class)))
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  public Inventory getPlayerInventory(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
  @GraphQLMutation(name = "addToPlayerInventory", description = addToPlayerInventoryDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The items were added successfully")
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  @AccessControl(Access.WRITE)
  public void addToPlayerInventory(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
  @GraphQLMutation(name = "removeFromPlayerInventory", description = removeFromPlayerInventoryDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The items were removed successfully")
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  @AccessControl(Access.WRITE)
  public void removeFromPlayerInventory(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
  @GraphQLNonNull
  @GraphQLQuery(name = "playerEnderChest", description = getPlayerEnderChestDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The inventory of the ender chest of the player",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Inventory.class)))
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  public Inventory getPlayerEnderChest(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
  @GraphQLMutation(name = "addToPlayerEnderChest", description = addToPlayerEnderChestDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The items were added successfully")
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  @AccessControl(Access.WRITE)
  public void addToPlayerEnderChest(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
  @GraphQLMutation(name = "removeFromPlayerEnderChest", description = removeFromPlayerEnderChestDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The items were removed successfully")
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The player with the given UUID was not found, or is not online")
  @AccessControl(Access.WRITE)
  public void removeFromPlayerEnderChest(
      @PathParam("player") @GraphQLNonNull @GraphQLArgument(name = "uuid", description = "The UUID of the player") String uuidString,
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
