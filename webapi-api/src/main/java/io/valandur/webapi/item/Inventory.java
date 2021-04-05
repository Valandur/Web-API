package io.valandur.webapi.item;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.item.ItemStack;

import java.util.Collection;

public class Inventory {

    @GraphQLQuery(name = "size", description = "The maximum amount of stacks this inventory can hold")
    public final int size;

    @GraphQLQuery(name = "items", description = "All items that this inventory contains")
    public final Collection<ItemStack> items;

    public Inventory(int size, Collection<ItemStack> items) {
        this.size = size;
        this.items = items;
    }
}
