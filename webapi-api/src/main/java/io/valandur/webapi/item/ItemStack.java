package io.valandur.webapi.item;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Collection;
import java.util.Map;

@GraphQLNonNull
public record ItemStack(
    @GraphQLNonNull
    @GraphQLQuery(description = typeDescr)
    @Schema(description = typeDescr, requiredMode = RequiredMode.REQUIRED)
    String type,
    @GraphQLNonNull
    @GraphQLQuery(description = amountDescr)
    @Schema(description = amountDescr, requiredMode = RequiredMode.REQUIRED)
    int amount,
    @GraphQLQuery(description = enchantmentsDescr)
    @Schema(description = enchantmentsDescr)
    Map<String, Integer> enchantments,
    @GraphQLQuery(description = displayNameDescr)
    @Schema(description = displayNameDescr)
    String displayName,
    @GraphQLQuery(description = damageDescr)
    @Schema(description = damageDescr)
    Integer damage,
    @GraphQLQuery(description = authorDescr)
    @Schema(description = authorDescr)
    String author,
    @GraphQLQuery(description = pagesDescr)
    @Schema(description = pagesDescr)
    Collection<String> pages) {

  private static final String typeDescr = "The item type, for example 'minecraft:dirt'";
  private static final String amountDescr = "The amount of the item in this stack";
  private static final String enchantmentsDescr = "Enchantments and their level attached to this item";
  private static final String displayNameDescr = "The display name of this item (if set)";
  private static final String damageDescr = "The damage this item has received (durability)";
  private static final String authorDescr = "The author of this book";
  private static final String pagesDescr = "The pages contained in this book";

}
