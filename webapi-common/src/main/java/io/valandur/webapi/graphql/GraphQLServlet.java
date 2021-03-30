package io.valandur.webapi.graphql;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.info.InfoServlet;
import io.valandur.webapi.player.PlayerServlet;
import io.valandur.webapi.user.UserServlet;
import io.valandur.webapi.world.WorldServlet;
import jakarta.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class GraphQLServlet extends HttpServlet {

    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
    private static final String APPLICATION_GRAPHQL = "application/graphql";
    private static final String APPLICATION_JSON = "application/json";

    private final WebAPI<?> webapi;
    private GraphQL graphQL;
    private ObjectMapper mapper;

    public GraphQLServlet() {
        super();

        this.webapi = WebAPI.getInstance();
    }

    /**
     * Initialize and configure GraphQL servlet.
     */
    @Override
    public void init() throws ServletException {
        //logger.debug("Initialize GraphQLServlet servlets.");
        mapper = new ObjectMapper(new JsonFactory());

        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("io.valandur.webapi")
                .withOperationsFromSingleton(new InfoServlet())
                .withOperationsFromSingleton(new PlayerServlet())
                .withOperationsFromSingleton(new UserServlet())
                .withOperationsFromSingleton(new WorldServlet())
                .generate();

        graphQL = GraphQL.newGraphQL(schema).build();
    }

    /**
     * Perform GraphQL queries with GET requests.
     *
     * @link https://graphql.org/learn/serving-over-http/#get-request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //logger.debug("GET GraphQL endpoint");
        String query = req.getParameter("query");
        String operationName = req.getParameter("operationName");
        String variables = req.getParameter("variables");

        // validate that we have query parameter.
        if (query == null || query.isEmpty()) {
            resp.getWriter().println("Missing query parameter.");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // validate that we have variables parameter.
        Map<String, Object> variableMap = new HashMap<>();
        if (variables != null && !variables.isEmpty()) {
            variableMap = mapper.readValue(variables, new TypeReference<Map<String, Object>>() {
            });
        }

        // validate that we have operationName parameter.
        operationName = operationName != null ? operationName : "";

        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variableMap)
                .operationName(operationName)
                .context(req.getUserPrincipal())
                .build();

        // execute query
        ExecutionResult executionResult = graphQL.execute(input);
        String jsonResult = mapper.writeValueAsString(executionResult.toSpecification());

        // write response
        resp.setContentType(APPLICATION_JSON_UTF8);
        resp.getWriter().println(jsonResult);
    }

    /**
     * Need to override this method and hard code the response because the servlets
     * default implementation use reflection to find out available methods from implemented
     * class methods and GraalVM native-image does not work with reflection.
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Allow", "TRACE, OPTIONS, GET, POST");
    }

    /**
     * Perform GraphQL queries with POST requests.
     * Accepts two slightly different query formats, application/json and application/graphql.
     * <p>
     * Format of a application/json body.
     * {
     * "query": "...",
     * "operationName": "...",
     * "variables": { "myVariable": "someValue", ... }
     * }
     *
     * @link https://graphql.org/learn/serving-over-http/#post-request for specification.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //logger.debug("POST GraphQL endpoint");
        String query = "";
        String operationName = "";
        Map<String, Object> variableMap = new HashMap<>();
        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        if (APPLICATION_JSON.equals(req.getContentType())) {
            Map<String, Object> bodyMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });

            // if query is present as request parameter use that instead of json query.
            query = (String) (req.getParameter("query") != null ? req.getParameter("query") : bodyMap.get("query"));
            operationName = (String) bodyMap.getOrDefault("operationName", "");
            variableMap = (Map<String, Object>) bodyMap.getOrDefault("variables", new HashMap<>());

        } else if (APPLICATION_GRAPHQL.equals(req.getContentType())) {
            query = body;
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Unknown content type.");
            resp.getWriter().println("This endpoint understands application/json and application/graphql " +
                    "content-types.");
            return;
        }

        // validate that we have query parameter.
        if (query == null || query.isEmpty()) {
            resp.getWriter().println("Missing query parameter.");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // execute query
        ExecutionInput input = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variableMap)
                .operationName(operationName)
                .context(req.getUserPrincipal())
                .build();
        ExecutionResult executionResult = graphQL.execute(input);

        // write response
        resp.setContentType(APPLICATION_JSON_UTF8);
        resp.getWriter().println(mapper.writeValueAsString(executionResult.toSpecification()));
    }
}
