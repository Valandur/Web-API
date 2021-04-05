package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.item.ItemStack;

public class Player {

    @GraphQLQuery(name = "uuid", description = "The UUID of this player")
    public final String uuid;

    @GraphQLQuery(name = "name", description = "The name of this player")
    public final String name;

    @GraphQLQuery(name = "address", description = "The remote address of the player")
    public final String address;

    @GraphQLQuery(name = "helmet", description = "The item that is in the helmet slot of this inventory")
    public final ItemStack helmet;

    @GraphQLQuery(name = "chestplate", description = "The item that is in the chestplate slot of this inventory")
    public final ItemStack chestplate;

    @GraphQLQuery(name = "leggings", description = "The item that is in the leggings slot of this inventory")
    public final ItemStack leggings;

    @GraphQLQuery(name = "boots", description = "The item that is in the boots slot of this inventory")
    public final ItemStack boots;

    public Player(String uuid, String name, String address, ItemStack helmet, ItemStack chestplate, ItemStack leggings,
                  ItemStack boots) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;

        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }
}
