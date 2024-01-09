package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.security.Access;
import io.valandur.webapi.security.AccessControl;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;

@Singleton
@Path("world")
@Tag(name = "World", description = WorldServlet.classDescr)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorldServlet extends BaseServlet {

  protected static final String classDescr = "Get worlds on the server";
  private static final String getWorldsDescr = "List all the worlds on the server";
  private static final String getWorldDescr = "Get the details of a world";
  private static final String getWorldConstantsDescr = "An object with various constants used by worlds";
  private static final String createWorldDescr = "Create a new world on the server";
  private static final String deleteWorldDesr = "Unload and delete an existing world on the server";
  private static final String updateWorldDescr = "Change world properties";
  private static final String getBlockDescr = "Get the block within the specified world at the specified position";
  private static final String setBlockDescr = "Sets the block within the specified world at the specified position to the specified block & state";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "worlds", description = getWorldsDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An array of worlds",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = World.class))))
  public Collection<World> getWorlds() {
    return worldService.getWorlds();
  }

  @GET
  @Path("const")
  @GraphQLNonNull
  @GraphQLQuery(name = "worldConstants", description = getWorldConstantsDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An object with various constants used by worlds",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = WorldConstants.class)))
  public WorldConstants getConstants() {
    return worldService.getConstants();
  }

  @POST
  @GraphQLNonNull
  @GraphQLMutation(name = "createWorld", description = createWorldDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The newly created world",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = World.class)))
  public World createWorld(
      @GraphQLNonNull @GraphQLArgument(name = "data", description = "The creation data for the world") CreateWorldData data
  ) {
    return worldService.createWorld(data);
  }

  @GET
  @Path("{world}")
  @GraphQLQuery(name = "world", description = getWorldDescr)
  @ApiResponse(
          responseCode = "200",
          description = "The details of the world",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = World.class)))
  public World getWorld(
          @PathParam("world") @GraphQLNonNull @GraphQLArgument(name = "world", description = "The name of the world") String worldName
  ) {
    return worldService.getWorld(worldName);
  }

  @DELETE
  @Path("{world}")
  @GraphQLMutation(name = "deleteWorld", description = deleteWorldDesr)
  @ApiResponse(
      responseCode = "200",
      description = "The world was successfully deleted")
  public void deleteWorld(
      @PathParam("world") @GraphQLNonNull @GraphQLArgument(name = "world", description = "The name of the world") String worldName
  ) {
    worldService.deleteWorld(worldName);
  }

  @PATCH
  @Path("{world}")
  @GraphQLMutation(name = "updateWorld", description = updateWorldDescr)
  @ApiResponse(
          responseCode = "200",
          description = "The world was successfully changed")
  public void updateWorld(
          @PathParam("world") @GraphQLNonNull @GraphQLArgument(name = "world", description = "The name of the world") String worldName,
          @QueryParam("loaded") @GraphQLArgument(name = "loaded", description = "True if the world should be loaded, false otherwise") Boolean loaded
  ) {
    var world = worldService.getWorld(worldName);
    if (loaded != null) {
      if (loaded && !world.loaded()) {
        worldService.loadWorld(worldName);
      } else if (!loaded && world.loaded()) {
        worldService.unloadWorld(worldName);
      }
    }
  }

  @GET
  @Path("{world}/{x}/{y}/{z}")
  @GraphQLNonNull
  @GraphQLQuery(name = "block", description = getBlockDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The block along with its state",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Block.class)))
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The world with the given UUID was not found")
  public Block getBlockAt(
      @PathParam("world") @GraphQLNonNull @GraphQLArgument(name = "world", description = "The name of the world") String worldName,
      @PathParam("x") @GraphQLNonNull @GraphQLArgument(name = "x", description = "The x coordinate") int x,
      @PathParam("y") @GraphQLNonNull @GraphQLArgument(name = "y", description = "The y coordinate") int y,
      @PathParam("z") @GraphQLNonNull @GraphQLArgument(name = "z", description = "The z coordinate") int z
  ) {
    return worldService.getBlockAt(worldName, x, y, z);
  }

  @PUT
  @Path("{world}/{x}/{y}/{z}")
  @GraphQLQuery(name = "setBlock", description = setBlockDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The block was updated successfully")
  @ApiResponse(
      responseCode = "400",
      description = "The provided UUID is invalid")
  @ApiResponse(
      responseCode = "404",
      description = "The world with the given UUID was not found")
  @AccessControl(Access.WRITE)
  public void setBlockAt(
      @PathParam("world") @GraphQLNonNull @GraphQLArgument(name = "world", description = "The name of the world") String worldName,
      @PathParam("x") @GraphQLNonNull @GraphQLArgument(name = "x", description = "The x coordinate") int x,
      @PathParam("y") @GraphQLNonNull @GraphQLArgument(name = "y", description = "The y coordinate") int y,
      @PathParam("z") @GraphQLNonNull @GraphQLArgument(name = "z", description = "The z coordinate") int z,
      @GraphQLNonNull @GraphQLArgument(name = "block", description = "The block to set") Block block
  ) {
    worldService.setBlockAt(worldName, x, y, z, block);
  }
}