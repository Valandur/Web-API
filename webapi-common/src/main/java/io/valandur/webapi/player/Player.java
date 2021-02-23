package io.valandur.webapi.player;

import io.valandur.webapi.item.ItemStack;

import java.util.Collection;

public class Player {
    public final String uuid;
    public final String name;
    public final String address;

    public final ItemStack helmet;
    public final ItemStack chestplate;
    public final ItemStack leggings;
    public final ItemStack boots;

    public final Collection<ItemStack> inventory;

    public Player(String uuid, String name, String address, ItemStack helmet, ItemStack chestplate,
                  ItemStack leggings, ItemStack boots, Collection<ItemStack> inventory) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;

        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;

        this.inventory = inventory;
    }
}
