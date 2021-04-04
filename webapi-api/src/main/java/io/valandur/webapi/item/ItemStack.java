package io.valandur.webapi.item;

import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.Collection;
import java.util.Map;

public class ItemStack {

    @GraphQLQuery(name = "type", description = "The item type, for example 'minecraft:dirt'")
    public String type;

    @GraphQLQuery(name = "amount", description = "The amount of the item in this stack")
    public int amount;

    @GraphQLQuery(name = "enchantments", description = "Enchantments and their level attached to this item")
    public Map<String, Integer> enchantments;

    @GraphQLQuery(name = "displayName", description = "The display name of this item (if set)")
    public String displayName;

    @GraphQLQuery(name = "damage", description = "The damage this item has received (durability)")
    public Integer damage;

    @GraphQLQuery(name = "author", description = "The author of this book")
    public String author;

    @GraphQLQuery(name = "pages", description = "The pages contained in this book")
    public Collection<String> pages;

    public ItemStack() {
    }

    public ItemStack(String type, int amount, Map<String, Integer> enchantments, String displayName, Integer damage,
                     String author, Collection<String> pages) {
        this.type = type;
        this.amount = amount;
        this.enchantments = enchantments;
        this.displayName = displayName;
        this.damage = damage;
        this.author = author;
        this.pages = pages;
    }
}
