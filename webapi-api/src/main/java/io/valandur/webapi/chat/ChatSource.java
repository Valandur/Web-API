package io.valandur.webapi.chat;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;

@GraphQLNonNull
public record ChatSource(
    @GraphQLQuery(description = playerIdDescr)
    @Schema(description = playerIdDescr)
    UUID playerId,
    @GraphQLQuery(description = commandDescr)
    @Schema(description = commandDescr)
    String command,
    @GraphQLNonNull
    @GraphQLQuery(description = isFromServerDescr)
    @Schema(description = isFromServerDescr, requiredMode = RequiredMode.REQUIRED)
    boolean isFromServer
) {

  private static final String playerIdDescr = "The UUID of the sending player, if any";
  private static final String commandDescr = "The originating command, if any";
  private static final String isFromServerDescr = "True if the message originated from the server, false otherwise";
}
