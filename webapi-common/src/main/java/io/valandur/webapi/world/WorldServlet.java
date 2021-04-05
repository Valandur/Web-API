package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("worlds")
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
            @PathParam("world") @GraphQLArgument(name = "world", description = "The world type") String world,
            @PathParam("x") @GraphQLArgument(name = "x", description = "The x coordinate") int x,
            @PathParam("y") @GraphQLArgument(name = "y", description = "The y coordinate") int y,
            @PathParam("z") @GraphQLArgument(name = "z", description = "The z coordinate") int z
    ) throws ExecutionException, InterruptedException {
        return webapi.runOnMain(() -> worldService.getBlockAt(world, x, y, z));
    }

    @PUT
    @Path("{world}/{x}/{y}/{z}")
    @GraphQLQuery(name = "setBlock")
    public void setBlockAt(
            @PathParam("world") @GraphQLArgument(name = "world", description = "The world type") String world,
            @PathParam("x") @GraphQLArgument(name = "x", description = "The x coordinate") int x,
            @PathParam("y") @GraphQLArgument(name = "y", description = "The y coordinate") int y,
            @PathParam("z") @GraphQLArgument(name = "z", description = "The z coordinate") int z,
            @GraphQLArgument(name = "block", description = "The block to set") Block block
    ) throws ExecutionException, InterruptedException {
        webapi.runOnMain(() -> worldService.setBlockAt(world, x, y, z, block));
    }
}
