package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLQuery;

public class Player {
    @GraphQLQuery(name = "uuid", description = "The UUID of this player")
    public final String uuid;

    @GraphQLQuery(name = "name", description = "The name of this player")
    public final String name;

    @GraphQLQuery(name = "address", description = "The remote address of the player")
    public final String address;

    public Player(String uuid, String name, String address) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }
}
