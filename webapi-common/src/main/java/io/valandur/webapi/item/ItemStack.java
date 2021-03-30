package io.valandur.webapi.item;

import java.util.Map;

public class ItemStack {
    public String type;
    public int amount;

    public Map<String, Integer> enchantments;
    public Map<String, Object> meta;

    public ItemStack() {
    }

    public ItemStack(String type, int amount, Map<String, Object> meta, Map<String, Integer> enchantments) {
        this.type = type;
        this.amount = amount;
        this.meta = meta;
        this.enchantments = enchantments;
    }
}
