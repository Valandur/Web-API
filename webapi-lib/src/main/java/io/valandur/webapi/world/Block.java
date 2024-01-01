package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@GraphQLNonNull
public record Block(
    @GraphQLNonNull
    @GraphQLQuery(description = typeDescr)
    @Schema(description = typeDescr, requiredMode = RequiredMode.REQUIRED)
    String type) {

  private static final String typeDescr = "The type of this block";

}
