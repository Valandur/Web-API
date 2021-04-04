package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
