package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.Collection;

public class World {

    @GraphQLQuery(name = "type", description = "The type of this world")
    public final String type;

    @GraphQLQuery(name = "name", description = "The name of this world")
    public final String name;

    @GraphQLQuery(name = "difficulty", description = "The difficulty setting of this world")
    public final String difficulty;

    @GraphQLQuery(name = "seed", description = "The seed used to generate this world")
    public final Long seed;

    @GraphQLQuery(name = "gameRules", description = "The game rules that are applicable for this world")
    public final Collection<GameRule> gameRules;

    public World(String type, String name, String difficulty, long seed, Collection<GameRule> gameRules) {
        this.type = type;
        this.name = name;

        this.difficulty = difficulty;

        this.seed = seed;
        this.gameRules = gameRules;
    }
}
