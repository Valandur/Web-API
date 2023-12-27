package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Collection;
import java.util.UUID;

public record World(
    @GraphQLNonNull
    @GraphQLQuery(description = uuidDescr)
    @Schema(description = uuidDescr, requiredMode = RequiredMode.REQUIRED)
    UUID uuid,
    @GraphQLNonNull
    @GraphQLQuery(description = typeDescr)
    @Schema(description = typeDescr, requiredMode = RequiredMode.REQUIRED)
    String type,
    @GraphQLQuery(description = nameDescr)
    @Schema(description = nameDescr)
    String name,
    @GraphQLNonNull
    @GraphQLQuery(description = difficultyDescr)
    @Schema(description = difficultyDescr, requiredMode = RequiredMode.REQUIRED)
    String difficulty,
    @GraphQLNonNull
    @GraphQLQuery(description = seedDescr)
    @Schema(description = seedDescr, requiredMode = RequiredMode.REQUIRED)
    String seed,
    @GraphQLNonNull
    @GraphQLQuery(description = gameRulesDescr)
    @Schema(description = gameRulesDescr, requiredMode = RequiredMode.REQUIRED)
    Collection<GameRule> gameRules) {

    private static final String uuidDescr = "The unique ID of the world";
    private static final String typeDescr = "The type of this world";
    private static final String nameDescr = "The name of this world";
    private static final String difficultyDescr = "The difficulty setting of this world";
    private static final String seedDescr = "The seed used to generate this world";
    private static final String gameRulesDescr = "The game rules that are applicable for this world";

}
