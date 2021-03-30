package io.valandur.webapi.user;

import io.leangen.graphql.annotations.GraphQLQuery;

public class User {
    @GraphQLQuery(name = "uuid", description = "The UUID of this user")
    public final String uuid;

    @GraphQLQuery(name = "name", description = "The name of this user")
    public final String name;

    public User(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
