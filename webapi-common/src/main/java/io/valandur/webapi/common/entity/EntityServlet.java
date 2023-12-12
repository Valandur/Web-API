package io.valandur.webapi.common.entity;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.entity.Entity;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("entity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityServlet extends BaseServlet {

  @GET
  @GraphQLQuery(name = "entities")
  public Collection<Entity> getEntities() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> entityService.getEntities());
  }
}
