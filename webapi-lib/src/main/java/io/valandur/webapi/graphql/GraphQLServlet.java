package io.valandur.webapi.graphql;

import graphql.ExecutionInput;
import graphql.GraphQL;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Singleton
@Path("test")
@Hidden
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphQLServlet extends BaseServlet {

  private final GraphQL graphQL;

  public GraphQLServlet() {
    super();

    var gen = new GraphQLSchemaGenerator().withBasePackages("io.valandur.webapi");
    for (var servlet : webapi.getServlets()) {
      gen.withOperationsFromSingleton(servlet);
    }

    graphQL = GraphQL.newGraphQL(gen.generate()).build();
  }

  @GET
  public Map<String, Object> indexGet(
      @QueryParam("query") String query,
      @QueryParam("operationName") String operationName
  ) {
    if (query == null) {
      throw new BadRequestException("Query must not be null");
    }
    var input = ExecutionInput.newExecutionInput()
        .query(query)
        .operationName(operationName);
    var executionResult = graphQL.execute(input.build());
    return executionResult.toSpecification();
  }

  @POST
  public Map<String, Object> indexPost(GraphQLRequest request) {
    if (request.query() == null) {
      throw new BadRequestException("Query must not be null");
    }
    var input = ExecutionInput.newExecutionInput()
        .query(request.query());
    if (request.operationName() != null) {
      input.operationName(request.operationName());
    }
    if (request.variables() != null) {
      input.variables(request.variables());
    }
    var executionResult = graphQL.execute(input.build());
    return executionResult.toSpecification();
  }
}
