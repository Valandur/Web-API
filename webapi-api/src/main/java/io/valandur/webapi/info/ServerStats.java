package io.valandur.webapi.info;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;

@GraphQLNonNull
public record ServerStats(
    @GraphQLNonNull
    @GraphQLQuery(description = timestampDescr)
    @Schema(description = timestampDescr, requiredMode = RequiredMode.REQUIRED)
    Instant timestamp,
    @GraphQLNonNull
    @GraphQLQuery(description = onlinePlayersDescr)
    @Schema(description = onlinePlayersDescr, requiredMode = RequiredMode.REQUIRED)
    int onlinePlayers,
    @GraphQLNonNull
    @GraphQLQuery(description = avgTpsDescr)
    @Schema(description = avgTpsDescr, requiredMode = RequiredMode.REQUIRED)
    double avgTps,
    @GraphQLNonNull
    @GraphQLQuery(description = cpuLoadDescr)
    @Schema(description = cpuLoadDescr, requiredMode = RequiredMode.REQUIRED)
    double cpuLoad,
    @GraphQLNonNull
    @GraphQLQuery(description = memLoadDescr)
    @Schema(description = memLoadDescr, requiredMode = RequiredMode.REQUIRED)
    double memLoad,
    @GraphQLNonNull
    @GraphQLQuery(description = diskUsageDescr)
    @Schema(description = diskUsageDescr, requiredMode = RequiredMode.REQUIRED)
    double diskUsage) {

  private static final String timestampDescr = "The timestamp when the stats were recorded";
  private static final String onlinePlayersDescr = "The amount of players online";
  private static final String avgTpsDescr = "The average amount of tps for the current period";
  private static final String cpuLoadDescr = "The average CPU load (0-1)";
  private static final String memLoadDescr = "The average memory load (0-1)";
  private static final String diskUsageDescr = "The current disk usage (0-1)";

}
