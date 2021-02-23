package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;
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
}
