package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.entity.Location;
import io.valandur.webapi.item.ItemStack;
import java.util.UUID;

public record Player(
    @GraphQLQuery(name = "uuid", description = "The UUID of this player") String uuid,
    @GraphQLQuery(name = "name", description = "The name of this player") String name,
    @GraphQLQuery(name = "worldId", description = "The ID of the world the player is currently in") UUID worldId,
    @GraphQLQuery(name = "location", description = "The location of this player within the world") Location location,
    @GraphQLQuery(name = "address", description = "The remote address of the player") String address,
    @GraphQLQuery(name = "helmet", description = "The item that is in the helmet slot of this inventory") ItemStack helmet,
    @GraphQLQuery(name = "chestplate", description = "The item that is in the chestplate slot of this inventory") ItemStack chestplate,
    @GraphQLQuery(name = "leggings", description = "The item that is in the leggings slot of this inventory") ItemStack leggings,
    @GraphQLQuery(name = "boots", description = "The item that is in the boots slot of this inventory") ItemStack boots) {

}
