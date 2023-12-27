package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;

@GraphQLNonNull
public record Location(
    @GraphQLNonNull
    @GraphQLQuery(description = worldDescr)
    @Schema(description = worldDescr, requiredMode = RequiredMode.REQUIRED)
    UUID world,
    @GraphQLNonNull
    @GraphQLQuery(description = positionDescr)
    @Schema(description = positionDescr, requiredMode = RequiredMode.REQUIRED)
    Position position) {

  private static final String worldDescr = "The UUID of the world";
  private static final String positionDescr = "The position within the world";
}
