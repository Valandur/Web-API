package io.valandur.webapi.common.server;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.info.ServerInfo;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerServlet extends BaseServlet {

  @GET
  @GraphQLQuery(name = "server")
  public ServerInfo getInfo() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> serverService.getInfo());
  }
}
