package io.valandur.webapi.item;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Collection;

@GraphQLNonNull
public record Inventory(
    @GraphQLNonNull
    @GraphQLQuery(description = sizeDescr)
    @Schema(description = sizeDescr, requiredMode = RequiredMode.REQUIRED)
    int size,
    @GraphQLNonNull
    @GraphQLQuery(description = itemsDescr)
    @Schema(description = itemsDescr, requiredMode = RequiredMode.REQUIRED)
    Collection<ItemStack> items) {

  private static final String sizeDescr = "The maximum amount of stacks this inventory can hold";
  private static final String itemsDescr = "All items that this inventory contains";

}
