package io.valandur.webapi.graphql;

import java.util.Map;

public record GraphQLRequest(
    String query,
    String operationName,
    Map<String, Object> variables
) {
}
