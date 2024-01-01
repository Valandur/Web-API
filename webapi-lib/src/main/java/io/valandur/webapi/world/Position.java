package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@GraphQLNonNull
public record Position(
    @GraphQLNonNull
    @GraphQLQuery(description = xDescr)
    @Schema(description = xDescr, requiredMode = RequiredMode.REQUIRED)
    double x,
    @GraphQLNonNull
    @GraphQLQuery(description = yDescr)
    @Schema(description = yDescr, requiredMode = RequiredMode.REQUIRED)
    double y,
    @GraphQLNonNull
    @GraphQLQuery(description = zDescr)
    @Schema(description = zDescr, requiredMode = RequiredMode.REQUIRED)
    double z) {

  private static final String xDescr = "The x-coordinate";
  private static final String yDescr = "The y-coordinate";
  private static final String zDescr = "The z-coordinate";

}
