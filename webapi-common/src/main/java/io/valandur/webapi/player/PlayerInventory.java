package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.item.ItemStack;

import java.util.Collection;

public class PlayerInventory {

    @GraphQLQuery(name = "helmet", description = "The item that is in the helmet slot of this inventory")
    public final ItemStack helmet;

    @GraphQLQuery(name = "chestplate", description = "The item that is in the chestplate slot of this inventory")
    public final ItemStack chestplate;

    @GraphQLQuery(name = "leggings", description = "The item that is in the leggings slot of this inventory")
    public final ItemStack leggings;

    @GraphQLQuery(name = "boots", description = "The item that is in the boots slot of this inventory")
    public final ItemStack boots;

    @GraphQLQuery(name = "items", description = "All items that this inventory contains")
    public final Collection<ItemStack> items;

    public PlayerInventory(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots,
                           Collection<ItemStack> items) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;

        this.items = items;
    }
}
