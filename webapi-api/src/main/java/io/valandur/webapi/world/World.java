package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Collection;
import java.util.UUID;

public record World(
    @GraphQLQuery(name = "uuid", description = "The unique ID of the world") UUID uuid,
    @GraphQLQuery(name = "type", description = "The type of this world") String type,
    @GraphQLQuery(name = "name", description = "The name of this world") String name,
    @GraphQLQuery(name = "difficulty", description = "The difficulty setting of this world") String difficulty,
    @GraphQLQuery(name = "seed", description = "The seed used to generate this world") Long seed,
    @GraphQLQuery(name = "gameRules", description = "The game rules that are applicable for this world") Collection<GameRule> gameRules) {

}
