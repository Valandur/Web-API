package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@GraphQLNonNull
public record GameRule(
    @GraphQLNonNull
    @GraphQLQuery(description = nameDescr)
    @Schema(description = nameDescr, requiredMode = RequiredMode.REQUIRED)
    String name,
    @GraphQLNonNull
    @GraphQLQuery(description = valueDescr)
    @Schema(description = valueDescr, requiredMode = RequiredMode.REQUIRED)
    Object value) {

  private static final String nameDescr = "The name of the rule";
  private static final String valueDescr = "The value of the game rule";

}
