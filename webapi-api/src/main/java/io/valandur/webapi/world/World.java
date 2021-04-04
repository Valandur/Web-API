package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.Collection;

public class World {
    @GraphQLQuery(name = "uuid", description = "The UUID of this world")
    public final String uuid;

    @GraphQLQuery(name = "name", description = "The name of this world")
    public final String name;

    @GraphQLQuery(name = "dimension", description = "The name of the dimension this world belongs to")
    public final String dimension;

    @GraphQLQuery(name = "difficulty", description = "The difficulty setting of this world")
    public final String difficulty;

    @GraphQLQuery(name = "seed", description = "The seed used to generate this world")
    public final Long seed;

    @GraphQLQuery(name = "gameRules", description = "The game rules that are applicable for this world")
    public final Collection<GameRule> gameRules;

    public World(String uuid, String name, String dimension, String difficulty, long seed,
                 Collection<GameRule> gameRules) {
        this.uuid = uuid;
        this.name = name;
        this.dimension = dimension;

        this.difficulty = difficulty;

        this.seed = seed;
        this.gameRules = gameRules;
    }
}
