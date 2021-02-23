package io.valandur.webapi.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ItemStack {
    public final String type;
    public final int amount;

    public final Map<String, Integer> enchantments;
    public final Map<String, Object> meta;
    public final Collection<String> lore;

    public ItemStack(String type, int amount) {
        this(type, amount, new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }

    public ItemStack(String type, int amount, Map<String, Object> meta, Map<String, Integer> enchantments,
                     Collection<String> lore) {
        this.type = type;
        this.amount = amount;
        this.meta = meta;
        this.enchantments = enchantments;
        this.lore = lore;
    }
}
