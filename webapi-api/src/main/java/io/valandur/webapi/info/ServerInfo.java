package io.valandur.webapi.info;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record ServerInfo(
        @GraphQLNonNull
        @GraphQLQuery(description = motdDescr)
        @Schema(description = motdDescr, requiredMode = RequiredMode.REQUIRED)
        String messageOfTheDay,
        @GraphQLNonNull
        @GraphQLQuery(description = playerCountDescr)
        @Schema(description = playerCountDescr, requiredMode = RequiredMode.REQUIRED)
        int playerCount,
        @GraphQLNonNull
        @GraphQLQuery(description = maxPlayersDescr)
        @Schema(description = maxPlayersDescr, requiredMode = RequiredMode.REQUIRED)
        int maxPlayers,
        @GraphQLNonNull
        @GraphQLQuery(description = onlineModeDescr)
        @Schema(description = onlineModeDescr, requiredMode = RequiredMode.REQUIRED)
        boolean onlineMode,
        @GraphQLNonNull
        @GraphQLQuery(description = uptimeDescr)
        @Schema(description = uptimeDescr, requiredMode = RequiredMode.REQUIRED)
        long uptime,
        @GraphQLNonNull
        @GraphQLQuery(description = tpsDescr)
        @Schema(description = tpsDescr, requiredMode = RequiredMode.REQUIRED)
        double tps,
        @GraphQLNonNull
        @GraphQLQuery(description = mcVersionDescr)
        @Schema(description = mcVersionDescr, requiredMode = RequiredMode.REQUIRED)
        String minecraftVersion,
        @GraphQLNonNull
        @GraphQLQuery(description = flavourDescr)
        @Schema(description = flavourDescr, requiredMode = RequiredMode.REQUIRED)
        String flavour,
        @GraphQLNonNull
        @GraphQLQuery(description = flavourVersionDescr)
        @Schema(description = flavourVersionDescr, requiredMode = RequiredMode.REQUIRED)
        String flavourVersion,
        @GraphQLNonNull
        @GraphQLQuery(description = webApiVersionDescr)
        @Schema(description = webApiVersionDescr, requiredMode = RequiredMode.REQUIRED)
        String webapiVersion) {

    private static final String motdDescr = "The message of the day set on the server";
    private static final String playerCountDescr = "The current amount of players on the server";
    private static final String maxPlayersDescr = "The maximum amount of players on the server";
    private static final String onlineModeDescr = "True if this server is in online mode, false otherwise";
    private static final String uptimeDescr = "The current uptime of the server, measured in milliseconds";
    private static final String tpsDescr = "The current TPS of the server";
    private static final String mcVersionDescr = "The minecraft version that the server is running";
    private static final String flavourDescr = "The type of minecraft running on the server (Sponge, Spigot, Fabric)";
    private static final String flavourVersionDescr = "The version of the flavour running";
    private static final String webApiVersionDescr = "The version of the Web-API plugin";

}
