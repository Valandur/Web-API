package io.valandur.webapi.entity;

import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.UUID;

public record Entity(
    @GraphQLQuery(name = "uuid", description = "The unique ID of the entity") UUID uuid,
    @GraphQLQuery(name = "type", description = "The type of the entity") String type,
    @GraphQLQuery(name = "worldId", description = "The ID of the world the entity is in") UUID worldId,
    @GraphQLQuery(name = "location", description = "The location of the entity with the world") Location location,
    @GraphQLQuery(name = "name", description = "The given name of the entity") String name) {

}
