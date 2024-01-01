package io.valandur.webapi.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.ExecutionInput;
import graphql.GraphQL;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import io.valandur.webapi.WebAPI;
import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Singleton
@Path("graphql")
@Hidden
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphQLServlet extends HttpServlet {

  private final GraphQL graphQL;

  public GraphQLServlet() {
    super();

    var gen = new GraphQLSchemaGenerator().withBasePackages("io.valandur.webapi");
    for (var servlet : WebAPI.getInstance().getServlets()) {
      gen.withOperationsFromSingleton(servlet);
    }

    graphQL = GraphQL.newGraphQL(gen.generate()).build();
  }

  @POST
  public Map<String, Object> indexFromAnnotated(JsonNode request) {
    var input = ExecutionInput.newExecutionInput()
        .query(request.get("query").asText());
    if (request.hasNonNull("operationName")) {
      input.operationName(request.get("operationName").asText());
    }
    var executionResult = graphQL.execute(input.build());
    return executionResult.toSpecification();
  }
}
