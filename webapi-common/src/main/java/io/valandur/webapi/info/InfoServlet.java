package io.valandur.webapi.info;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.ExecutionException;

@Singleton
@Path("info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoServlet extends BaseServlet {

    @GET
    @GraphQLQuery(name = "info")
    public ServerInfo getInfo() throws ExecutionException, InterruptedException {
        return webapi.runOnMain(() -> webapi.getInfo());
    }
}
