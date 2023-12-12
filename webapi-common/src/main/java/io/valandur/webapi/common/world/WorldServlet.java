package io.valandur.webapi.common.world;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.security.Access;
import io.valandur.webapi.common.security.AccessControl;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.world.Block;
import io.valandur.webapi.world.World;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.jvnet.hk2.annotations.Service;

@Singleton
@Path("world")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorldServlet extends BaseServlet {

  @GET
  @GraphQLQuery(name = "worlds")
  public Collection<World> getWorlds() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> worldService.getWorlds());
  }

  @GET
  @Path("{world}/{x}/{y}/{z}")
  @GraphQLQuery(name = "block")
  public Block getBlockAt(
      @PathParam("world") @GraphQLArgument(name = "world", description = "The world ID") UUID worldId,
      @PathParam("x") @GraphQLArgument(name = "x", description = "The x coordinate") int x,
      @PathParam("y") @GraphQLArgument(name = "y", description = "The y coordinate") int y,
      @PathParam("z") @GraphQLArgument(name = "z", description = "The z coordinate") int z
  ) throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> worldService.getBlockAt(worldId, x, y, z));
  }

  @PUT
  @Path("{world}/{x}/{y}/{z}")
  @GraphQLQuery(name = "setBlock")
  @AccessControl(Access.WRITE)
  public void setBlockAt(
      @PathParam("world") @GraphQLArgument(name = "world", description = "The world ID") UUID worldId,
      @PathParam("x") @GraphQLArgument(name = "x", description = "The x coordinate") int x,
      @PathParam("y") @GraphQLArgument(name = "y", description = "The y coordinate") int y,
      @PathParam("z") @GraphQLArgument(name = "z", description = "The z coordinate") int z,
      @GraphQLArgument(name = "block", description = "The block to set") Block block
  ) throws ExecutionException, InterruptedException {
    webapi.runOnMain(() -> worldService.setBlockAt(worldId, x, y, z, block));
  }
}
