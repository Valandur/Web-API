package io.valandur.webapi.common.info;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.info.ServerStats;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Info", description = InfoServlet.classDescr)
public class InfoServlet extends BaseServlet {

  protected static final String classDescr = "Get server information & statistics";
  private static final String getInfoDescr = "Gets general info about the minecraft server";
  private static final String getStatsDescr = "Get server statistics collected over a period of time";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "info", description = getInfoDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An object with general server information",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ServerInfo.class)))
  public ServerInfo getInfo() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> infoService.getInfo());
  }

  @GET
  @Path("stats")
  @GraphQLNonNull
  @GraphQLQuery(name = "stats", description = getStatsDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An array of recorded server stats",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = ServerStats.class))))
  public List<ServerStats> getStats() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> infoService.getStats());
  }
}
