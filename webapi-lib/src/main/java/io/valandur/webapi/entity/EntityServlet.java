package io.valandur.webapi.entity;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;

@Singleton
@Path("entity")
@Tag(name = "Entity", description = EntityServlet.classDescr)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntityServlet extends BaseServlet {

  protected static final String classDescr = "Get entities on the server";
  private static final String getEntitiesDescr = "Get all entities on the server";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "entities", description = getEntitiesDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An array of entities",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Entity.class))))
  public Collection<Entity> getEntities() {
    return entityService.getEntities();
  }
}
