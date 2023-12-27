package io.valandur.webapi.entity;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.valandur.webapi.world.Location;
import java.util.UUID;

@GraphQLNonNull
public record Entity(
    @GraphQLNonNull
    @GraphQLQuery(description = uuidDescr)
    @Schema(description = uuidDescr, requiredMode = RequiredMode.REQUIRED)
    UUID uuid,
    @GraphQLNonNull
    @GraphQLQuery(description = typeDescr)
    @Schema(description = typeDescr, requiredMode = RequiredMode.REQUIRED)
    String type,
    @GraphQLNonNull
    @GraphQLQuery(description = locationDescr)
    @Schema(description = locationDescr, requiredMode = RequiredMode.REQUIRED)
    Location location,
    @GraphQLQuery(description = nameDescr)
    @Schema(description = nameDescr)
    String name) {

  private static final String uuidDescr = "The unique ID of the entity";
  private static final String typeDescr = "The type of the entity";
  private static final String locationDescr = "The location of the entity (world & position)";
  private static final String nameDescr = "The given name of the entity";
}
