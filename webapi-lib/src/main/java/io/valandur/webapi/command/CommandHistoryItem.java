package io.valandur.webapi.command;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record CommandHistoryItem(
        @GraphQLNonNull
        @GraphQLQuery(description = timestampDescr)
        @Schema(description = timestampDescr, requiredMode = Schema.RequiredMode.REQUIRED)
        Instant timestamp,
        @GraphQLNonNull
        @GraphQLQuery(description = commandDescr)
        @Schema(description = commandDescr, requiredMode = Schema.RequiredMode.REQUIRED)
        String command,
        @GraphQLNonNull
        @GraphQLQuery(description = sourceDescr)
        @Schema(description = sourceDescr, requiredMode = Schema.RequiredMode.REQUIRED)
        CommandSource source) {

    private static final String timestampDescr = "The server time at which this command was executed";
    private static final String commandDescr = "The actual command that was executed";
    private static final String sourceDescr = "The sender of this command";
}
