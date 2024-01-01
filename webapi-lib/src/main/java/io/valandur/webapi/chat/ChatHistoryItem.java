package io.valandur.webapi.chat;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;

@GraphQLNonNull
public record ChatHistoryItem(
    @GraphQLNonNull
    @GraphQLQuery(description = timestampDescr)
    @Schema(description = timestampDescr, requiredMode = RequiredMode.REQUIRED)
    Instant timestamp,
    @GraphQLNonNull
    @GraphQLQuery(description = messageDescr)
    @Schema(description = messageDescr, requiredMode = RequiredMode.REQUIRED)
    String message,
    @GraphQLNonNull
    @GraphQLQuery(description = sourceDescr)
    @Schema(description = sourceDescr, requiredMode = RequiredMode.REQUIRED)
    ChatSource source) {

  private static final String timestampDescr = "The server time at which this command was sent";
  private static final String messageDescr = "The actual text content of the command";
  private static final String sourceDescr = "The sender of this command";

}
