package io.valandur.webapi.item;

import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Collection;

public record Inventory(
    @GraphQLQuery(name = "size", description = "The maximum amount of stacks this inventory can hold") int size,
    @GraphQLQuery(name = "items", description = "All items that this inventory contains") Collection<ItemStack> items) {

}
